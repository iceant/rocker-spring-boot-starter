package com.pointcx.rocker.spring.boot.starter.reload;

import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.TemplateNotFoundException;
import com.fizzed.rocker.runtime.DefaultRockerBootstrap;
import com.fizzed.rocker.runtime.DefaultRockerModel;
import com.fizzed.rocker.runtime.DefaultRockerTemplate;
import com.fizzed.rocker.runtime.RockerBootstrap;
import com.pointcx.rocker.spring.boot.starter.RockerProperties;

import java.lang.reflect.Constructor;

public class SpringRockerDefaultBootstrap implements RockerBootstrap {
    private RockerProperties properties;

    public SpringRockerDefaultBootstrap() {
    }

    public SpringRockerDefaultBootstrap(RockerProperties properties) {
        this.properties = properties;
    }

    public RockerProperties getProperties() {
        return properties;
    }

    public void setProperties(RockerProperties properties) {
        this.properties = properties;
    }

    protected DefaultRockerTemplate buildTemplate(Class modelType, DefaultRockerModel model, ClassLoader classLoader) throws RenderingException {
        try {
            Class<?> templateType = Class.forName(modelType.getName() + "$Template", false, classLoader);

            Constructor<?> templateConstructor = templateType.getConstructor(modelType);

            return (DefaultRockerTemplate)templateConstructor.newInstance(model);
        } catch (Exception e) {
            throw new RenderingException("Unable to load template class", e);
        }
    }

    @Override
    public DefaultRockerTemplate template(Class modelType, DefaultRockerModel model) throws RenderingException {

        return buildTemplate(modelType, model, modelType.getClassLoader());

    }

    static public String templatePathToClassName(String templateName) {
        if (templateName == null) {
            throw new NullPointerException("Template name was null");
        }

        // views/app/index.rocker.html
        int pos = templateName.indexOf('.');
        if (pos < 0) {
            throw new IllegalArgumentException("Invalid template name '" + templateName + "'. Expecting something like 'views/app/index.rocker.html')");
        }

        String templateNameNoExt = templateName.substring(0, pos);

        // Chen Peng: We don't need this restriction
//        String templateExt = templateName.substring(pos);

//        if (!templateExt.startsWith(".rocker.")) {
//            throw new IllegalArgumentException("Invalid template extension '" + templateExt + "'. Expecting something like 'views/app/index.rocker.html')");
//        }


        return templateNameNoExt.replace('/', '.');
    }

    public RockerModel buildModel(String templatePath, ClassLoader classLoader) {
        // views/app/index.rocker.html -> views.app.index
        String modelClassName = templatePathToClassName(templatePath);

        Class<?> modelType = null;
        try {
            modelType = Class.forName(modelClassName, false, classLoader);
        } catch (ClassNotFoundException e) {
            throw new TemplateNotFoundException("Compiled template " + templatePath + " not found", e);
        }

        try {
            return (RockerModel)modelType.newInstance();
        } catch (Exception e) {
            throw new TemplateBindException(templatePath, modelClassName, "Unable to create model for template " + templatePath, e);
        }
    }

    @Override
    public RockerModel model(String templatePath) throws TemplateNotFoundException, TemplateBindException {

        return buildModel(templatePath, DefaultRockerBootstrap.class.getClassLoader());

    }
}
