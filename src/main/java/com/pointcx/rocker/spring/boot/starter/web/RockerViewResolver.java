package com.pointcx.rocker.spring.boot.starter.web;

import com.fizzed.rocker.runtime.RockerBootstrap;
import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.view.UrlBasedViewResolver;

import java.util.Locale;

public class RockerViewResolver extends UrlBasedViewResolver {
    private RockerBootstrap rockerBootstrap;
    private RockerProperties properties;

    public RockerViewResolver(RockerBootstrap rockerBootstrap, RockerProperties properties) {
        this.rockerBootstrap = rockerBootstrap;
        this.properties = properties;
        setViewClass(RockerView.class);
    }

    public RockerBootstrap getRockerBootstrap() {
        return rockerBootstrap;
    }

    public void setRockerBootstrap(RockerBootstrap rockerBootstrap) {
        this.rockerBootstrap = rockerBootstrap;
    }

    public RockerProperties getProperties() {
        return properties;
    }

    public void setProperties(RockerProperties properties) {
        this.properties = properties;
    }

    @Override
    protected View loadView(String viewName, Locale locale) throws Exception {
        RockerView view = new RockerView(viewName, rockerBootstrap, properties);
        view.setApplicationContext(getApplicationContext());
        view.setServletContext(getServletContext());
        view.setExposeSpringMacroHelpers(properties.isExposeSpringMacroHelpers());
        view.setExposeSessionAttributes(properties.isExposeSessionAttributes());
        view.setExposeRequestAttributes(properties.isExposeRequestAttributes());
        view.setAllowRequestOverride(properties.isAllowRequestOverride());
        view.setAllowSessionOverride(properties.isAllowSessionOverride());
        return view;
    }
}
