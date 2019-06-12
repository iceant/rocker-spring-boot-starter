package com.pointcx.rocker.spring.boot.starter;

import com.fizzed.rocker.BindableRockerModel;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.runtime.RockerBootstrap;

import java.lang.reflect.Method;

public class SpringRocker {
    private final RockerBootstrap rockerBootstrap;

    public SpringRocker(RockerBootstrap rockerBootstrap) {
        this.rockerBootstrap = rockerBootstrap;
    }

    public BindableRockerModel template(String templatePath){
        RockerModel model = rockerBootstrap.model(templatePath);
        return new BindableRockerModel(templatePath, model.getClass().getCanonicalName(), model);
    }

    public BindableRockerModel template(String templatePath, Object ... arguments) {

        // load model from bootstrap (which may recompile if needed)
        RockerModel model = rockerBootstrap.model(templatePath);

        BindableRockerModel bindableModel = new BindableRockerModel(templatePath, model.getClass().getCanonicalName(), model);

        if (arguments != null && arguments.length > 0) {
            String[] argumentNames = getModelArgumentNames(templatePath, model);

            if (arguments.length != argumentNames.length) {
                throw new TemplateBindException(templatePath, model.getClass().getCanonicalName(), "Template requires " + argumentNames.length + " arguments but " + arguments.length + " provided");
            }

            for (int i = 0; i < arguments.length; i++) {
                String name = argumentNames[i];
                Object value = arguments[i];
                bindableModel.bind(name, value);
            }
        }

        return bindableModel;
    }

    static private String[] getModelArgumentNames(String templatePath, RockerModel model) {
        try {
            Method f = model.getClass().getMethod("getArgumentNames");
            return (String[])f.invoke(null);
        } catch (Exception e) {
            throw new TemplateBindException(templatePath, model.getClass().getCanonicalName(), "Unable to read getModifiedAt static method from template");
        }
    }
}
