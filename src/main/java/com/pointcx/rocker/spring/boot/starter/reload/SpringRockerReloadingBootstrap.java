package com.pointcx.rocker.spring.boot.starter.reload;

import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.TemplateNotFoundException;
import com.fizzed.rocker.compiler.GeneratorException;
import com.fizzed.rocker.compiler.RockerConfiguration;
import com.fizzed.rocker.runtime.*;
import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import com.pointcx.rocker.spring.boot.starter.compiler.RockerTemplateCompiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class SpringRockerReloadingBootstrap extends SpringRockerDefaultBootstrap {
    static private final Logger log = LoggerFactory.getLogger(SpringRockerReloadingBootstrap.class);

    static public class LoadedTemplate {
        public File file;                           // resolved path to template file
        public long modifiedAt;                     // modified date of version loaded
        public String headerHash;                   // hash of header (interface)
    }

    private final RockerConfiguration configuration;
    private final ConcurrentHashMap<String,String> models;
    private final ConcurrentHashMap<String, SpringRockerReloadingBootstrap.LoadedTemplate> templates;
    private RockerClassLoader classLoader;

    public SpringRockerReloadingBootstrap(RockerProperties properties) {
        super(properties);
        this.configuration = new RockerConfiguration();
        this.models = new ConcurrentHashMap<>();
        this.templates = new ConcurrentHashMap<>();
        this.classLoader = buildClassLoader();
        if(properties.getTemplateDirectory()!=null) {
            File templateDirectory = new File(properties.getTemplateDirectory());
            if(templateDirectory.exists()){
                this.configuration.setTemplateDirectory(templateDirectory);
            }
        }
        if(properties.getClassDirectory()!=null) {
            File classDirectory = new File(properties.getClassDirectory());
            if(classDirectory.exists()) {
                this.configuration.setClassDirectory(classDirectory);
            }
        }
        if(properties.getOutputDirectory()!=null) {
            File outputDirectory = new File(properties.getOutputDirectory());
            if (outputDirectory.exists()) {
                this.configuration.setOutputDirectory(outputDirectory);
            }
        }
    }

    public SpringRockerReloadingBootstrap() {
        this.configuration = new RockerConfiguration();
        this.models = new ConcurrentHashMap<>();
        this.templates = new ConcurrentHashMap<>();
        this.classLoader = buildClassLoader();
    }

    public RockerConfiguration getConfiguration() {
        return configuration;
    }

    private RockerClassLoader buildClassLoader() {
        return new RockerClassLoader(this, getClass().getClassLoader());
    }

    // views.index$Template
    // views.index$PlainText
    // views.index$Template$1 (if something like an inner class)
    public boolean isReloadableClass(String className) {

        if (this.models.containsKey(className)) {
            return true;
        }

        // find first occurrence of $
        int pos = className.indexOf('$');
        if (pos < 0) {
            return false;
        }

        String modelClassName = className.substring(0, pos);

        return this.templates.containsKey(modelClassName);
    }

    public File getTemplateFile(String templatePackageName, String templateName) {
        File templateFileDirectory = new File(this.configuration.getTemplateDirectory(), templatePackageName.replace('.', '/'));
        return new File(templateFileDirectory, templateName);
    }

    private long getModelClassModifiedAt(Class modelType) throws RenderingException {
        try {
            Method m = modelType.getMethod("getModifiedAt");
            return (long) m.invoke(null);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RenderingException("Unable to read getModifiedAt static method from class " + modelType.getName());
        }
    }

    private String getModelClassHeaderHash(Class modelType) throws RenderingException {
        try {
            Method m = modelType.getMethod("getHeaderHash");
            return (String)m.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RenderingException("Unable to read getHeaderHash static method from class " + modelType.getName());
        }
    }

    private String getModelClassTemplatePackageName(Class modelType) throws RenderingException {
        try {
            Method m = modelType.getMethod("getTemplatePackageName");
            return (String)m.invoke(null);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RenderingException("Unable to read getTemplatePackageName static method from class " + modelType.getName());
        }
    }

    private String getModelClassTemplateName(Class modelType) throws RenderingException {
        try {
            Method m = modelType.getMethod("getTemplateName");
            return (String)m.invoke(null);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RenderingException("Unable to read getTemplateName static method from class " + modelType.getName());
        }
    }

    @Override
    public DefaultRockerTemplate template(Class modelType, DefaultRockerModel model) throws RenderingException {

        SpringRockerReloadingBootstrap.LoadedTemplate template = templates.get(modelType.getName());

        if (template == null) {
            // read stored "metadata" compiled with template as static fields
            String templatePackageName = this.getModelClassTemplatePackageName(modelType);
            String templateName = this.getModelClassTemplateName(modelType);
            long modifiedAt = this.getModelClassModifiedAt(modelType);
            String headerHash = this.getModelClassHeaderHash(modelType);

            File templateFile = getTemplateFile(templatePackageName, templateName);

            if (!templateFile.exists()) {
                log.warn("{}: does not exist for model {}. Unable to check if reload required", templateFile, modelType.getCanonicalName());
                return buildTemplate(modelType, model, this.classLoader);
            }

            template = new SpringRockerReloadingBootstrap.LoadedTemplate();
            template.file = templateFile;
            template.modifiedAt = modifiedAt;
            template.headerHash = headerHash;

            templates.put(modelType.getName(), template);

        } else {

            if (!template.file.exists()) {
                log.warn("{}: no longer exists for model {} (did you delete it?)", template.file, modelType.getCanonicalName());
                return buildTemplate(modelType, model, this.classLoader);
            }

        }

        compileIfNeeded(template, true);

        return buildTemplate(modelType, model, this.classLoader);
    }

    @Override
    public RockerModel model(String templatePath) throws TemplateNotFoundException, TemplateBindException {

        String modelClassName = SpringRockerDefaultBootstrap.templatePathToClassName(templatePath);

        SpringRockerReloadingBootstrap.LoadedTemplate template = templates.get(modelClassName);

        RockerModel initialModel = null;

        if (template == null) {
            File templateFile = new File(this.configuration.getTemplateDirectory(), templatePath);

            if (!templateFile.exists()) {
                log.warn("{}: does not exist. Unable to check if reload required", templateFile);
                return buildModel(templatePath, this.classLoader);
            }

            // load initial model so we can grab the metadata
            template = new SpringRockerReloadingBootstrap.LoadedTemplate();
            template.file = templateFile;
            template.modifiedAt = -1;           // maybe its not even compiled yet
            templates.put(modelClassName, template);

            // also add to models so that classloader knows to load it
            this.models.put(modelClassName, "");

            try {
                // update the template with the initially loaded modifed_at value
                initialModel = buildModel(templatePath, this.classLoader);
                template.modifiedAt = this.getModelClassModifiedAt(initialModel.getClass());
                template.headerHash = this.getModelClassHeaderHash(initialModel.getClass());
            } catch (Exception e) {
                // ignore exceptions here...
            }

        } else {

            if (!template.file.exists()) {
                log.warn("{}: no longer exists for model {} (did you delete it?)", template.file, modelClassName);
                return buildModel(templatePath, this.classLoader);
            }

        }

        boolean recompiled = compileIfNeeded(template, false);

        if (initialModel != null && !recompiled) {
            return initialModel;
        } else {
            // build a new one since it was recompiled
            return buildModel(templatePath, this.classLoader);
        }
    }

    public boolean compileIfNeeded(SpringRockerReloadingBootstrap.LoadedTemplate template, boolean verifyHeaderHash) {
        // recompile needed?
        long modifiedAt = template.file.lastModified();

        if (modifiedAt != template.modifiedAt) {

            log.info("Rocker template change detected [{}]", template.file);

            RockerTemplateCompiler compiler = new RockerTemplateCompiler(this.configuration);

            try {
                long start = System.currentTimeMillis();

                List<RockerTemplateCompiler.CompilationUnit> units
                        = compiler.parse(Arrays.asList(template.file));

                // did the interface change?
                RockerTemplateCompiler.CompilationUnit unit = units.get(0);
                String newHeaderHash = unit.getTemplateModel().createHeaderHash()+"";

                if (verifyHeaderHash) {
                    if (!newHeaderHash.equals(template.headerHash)) {
                        log.debug("current header hash " + template.headerHash + "; new header hash " + newHeaderHash);

                        // build proper template exception
                        String templatePath = unit.getTemplateModel().getPackageName().replace('.', '/');
                        throw new RenderingException(1, 1, unit.getTemplateModel().getTemplateName(), templatePath,
                                "Interface (e.g. arguments/imports) were modified. Unable to safely hot reload. Do a fresh project build and JVM restart.", null);
                    }
                }

                compiler.generate(units);

                compiler.compile(units);

                long stop = System.currentTimeMillis();

                log.info("Rocker compiled " + units.size() + " templates in " + (stop - start) + " ms");

                // save current modifiedAt & header hash
                template.modifiedAt = modifiedAt;
                template.headerHash = newHeaderHash;
            } catch (ParserException | CompileDiagnosticException | CompileUnrecoverableException e) {
                throw e;
            } catch (IOException | GeneratorException e) {
                throw new RenderingException("Unable to compile rocker template", e);
            }

            // create new classloader to force a new class load
            this.classLoader = buildClassLoader();

            return true;
        }

        return false;
    }
}
