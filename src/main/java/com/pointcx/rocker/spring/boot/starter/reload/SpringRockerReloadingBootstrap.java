package com.pointcx.rocker.spring.boot.starter.reload;

import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.TemplateNotFoundException;
import com.fizzed.rocker.compiler.GeneratorException;
import com.fizzed.rocker.compiler.RockerConfiguration;
import com.fizzed.rocker.compiler.RockerOptions;
import com.fizzed.rocker.compiler.TokenException;
import com.fizzed.rocker.runtime.*;
import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import com.pointcx.rocker.spring.boot.starter.compiler.RockerTemplateCompiler;
import com.pointcx.rocker.spring.boot.starter.util.RockerInternalUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.JarURLConnection;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringRockerReloadingBootstrap implements RockerReloadableBootstrap, ApplicationContextAware {

    public static class LoadedTemplate {
        public String path = null;
        public long modifiedAt = -1;
        public String headerHash;
    }


    static private final Logger log = LoggerFactory.getLogger(SpringRockerReloadingBootstrap.class);

    private ApplicationContext applicationContext;
    private RockerConfiguration configuration;
    private RockerProperties properties;
    private ClassLoader classLoader;
    private Map<String, LoadedTemplate> templates;
    private Map<String, String> models;

    public SpringRockerReloadingBootstrap(RockerProperties properties) {
        this(properties, null);
    }

    public SpringRockerReloadingBootstrap(RockerProperties properties, ClassLoader classLoader) {
        this.properties = properties;
        this.configuration = new RockerConfiguration();
        this.classLoader = (classLoader == null) ? buildClassLoader() : classLoader;
        this.templates = new ConcurrentHashMap<>();
        this.models = new ConcurrentHashMap<>();

        if(properties.getTemplateDirectory()!=null){
            this.configuration.setTemplateDirectory(new File(properties.getTemplateDirectory()));
        }
        if(properties.getClassDirectory()!=null){
            this.configuration.setClassDirectory(new File(properties.getClassDirectory()));
        }
        if(properties.getOutputDirectory()!=null){
            this.configuration.setOutputDirectory(new File(properties.getOutputDirectory()));
        }
        RockerOptions options = this.configuration.getOptions();
        options.setDiscardLogicWhitespace(properties.isDiscardLogicWhitespace());
        if(properties.getExtendsClass()!=null) {
            options.setExtendsClass(properties.getExtendsClass());
        }
        if(properties.getExtendsModelClass()!=null) {
            options.setExtendsModelClass(properties.getExtendsModelClass());
        }
        options.setOptimize(properties.isOptimize());
        options.setTargetCharset(properties.getTargetCharset());
        try {
            options.setJavaVersion(properties.getJavaVersion());
        } catch (TokenException e) {
            e.printStackTrace();
        }

        this.configuration.setOptions(options);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public RockerModel model(String templatePath) throws TemplateNotFoundException, TemplateBindException {
        String modelClassName = templatePathToClassName(templatePath);

        RockerModel rockerModel = null;

        LoadedTemplate template = templates.get(modelClassName);
        if (template == null) {
            template = new LoadedTemplate();
            template.path = templatePath;
            template.modifiedAt = -1;
            templates.put(modelClassName, template);
            models.put(modelClassName, "");
            try {
                // load RockerModel class from classpath
                rockerModel = buildModel(templatePath, this.classLoader);
                Class modelClass = rockerModel.getClass();
                template.modifiedAt = getModelClassModifiedAt(modelClass);
                template.headerHash = getModelClassHeaderHash(modelClass);
            } catch (Exception err) {
                // ignore exception
            }
        }

        boolean recompiled = compileIfNeeded(template, false);

        if (rockerModel != null && !recompiled) {
            return rockerModel;
        } else {
            // build a new one since it was recompiled
            return buildModel(templatePath, this.classLoader);
        }
    }


    @Override
    public DefaultRockerTemplate template(Class modelType, DefaultRockerModel model) throws RenderingException {
        return null;
    }

    @Override
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

    //////////////////////////////////////////////////////////////////////////
    ////
    public ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    public ClassLoader buildClassLoader() {
        return new RockerClassLoader(this, Thread.currentThread().getContextClassLoader());
    }

    // -----------------------------------------------------------------------
    // <prefix>/path/to/index<.rocker.html> -> path/to/index -> path.to.index
    public String templatePathToClassName(String templatePath) {
        if (templatePath == null) {
            throw new NullPointerException("Template name was null");
        }
        String prefix = properties.getPrefix();
        String suffix = properties.getSuffix();

        String templateName = templatePath;

        if (prefix!=null && prefix.length()>0 && templateName.startsWith(prefix)) {
            templateName = templateName.substring(prefix.length() + 1);
        }
        if (suffix!=null && suffix.length()>0 && templateName.endsWith(suffix)) {
            templateName = templateName.substring(0, templateName.length() - suffix.length());
        }

        templateName = templateName.replace('\\', '/');
        return templateName.replace('/', '.');
    }

    public RockerModel buildModel(String templatePath, ClassLoader classLoader) {
        String modelClassName = templatePathToClassName(templatePath);
        Class<?> modelType = null;
        try {
            modelType = Class.forName(modelClassName, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new TemplateNotFoundException("Compiled template " + templatePath + " not found", e);
        }

        try {
            return (RockerModel) modelType.newInstance();
        } catch (Exception e) {
            throw new TemplateBindException(templatePath, modelClassName, "Unable to create model for template " + templatePath, e);
        }
    }

    private static long getLastModified(URLConnection con) throws IOException {
        if (con instanceof JarURLConnection) {
            return ((JarURLConnection) con).getJarEntry().getTime();
        } else {
            return con.getLastModified();
        }
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
            return (String) m.invoke(null);
        } catch (NoSuchMethodException | SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
            throw new RenderingException("Unable to read getHeaderHash static method from class " + modelType.getName());
        }
    }

    private String getModelClassTemplatePackageName(Class modelType) throws RenderingException {
        try {
            Method m = modelType.getMethod("getTemplatePackageName");
            return (String) m.invoke(null);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new RenderingException("Unable to read getTemplatePackageName static method from class " + modelType.getName());
        }
    }

    private String getModelClassTemplateName(Class modelType) throws RenderingException {
        try {
            Method m = modelType.getMethod("getTemplateName");
            return (String) m.invoke(null);
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new RenderingException("Unable to read getTemplateName static method from class " + modelType.getName());
        }
    }

    public boolean compileIfNeeded(LoadedTemplate template, boolean verifyHeaderHash) {
        // recompile needed?

        Resource resource = RockerInternalUtil.resolveResource(template.path, LocaleContextHolder.getLocale(), applicationContext, properties);
        if(resource==null || !resource.exists()){
            return false;
        }

        long modifiedAt = -1;
        try {
            modifiedAt = resource.lastModified();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (modifiedAt != template.modifiedAt) {

            log.info("Rocker template change detected [{}]", template.path);

            RockerTemplateCompiler compiler = new RockerTemplateCompiler(this.configuration);

            try {
                long start = System.currentTimeMillis();

                List<RockerTemplateCompiler.CompilationUnit> units = compiler.parse(Arrays.asList(resource.getFile()));

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
            this.classLoader = null;
            this.classLoader = buildClassLoader();

            return true;
        }

        return false;
    }
}
