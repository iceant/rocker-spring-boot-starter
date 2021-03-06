package com.pointcx.rocker.spring.boot.starter.util;

import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.core.io.Resource;

import java.util.Locale;

public class RockerInternalUtil {

    static final FileSystemResourceLoader fileResourceLoader = new FileSystemResourceLoader();

    public static Resource resolveResource(String viewName, Locale locale, ApplicationContext applicationContext, RockerProperties rockerProperties) {
        String l10n = "";
        if (locale != null) {
            LocaleEditor localeEditor = new LocaleEditor();
            localeEditor.setValue(locale);
            l10n = "_" + localeEditor.getAsText();
        }
        return resolveFromLocale(viewName, l10n, applicationContext, rockerProperties);
    }

    private static Resource resolveFromLocale(String viewName, String locale, ApplicationContext applicationContext, RockerProperties rockerProperties) {
        String templateDirectory = rockerProperties.getTemplateDirectory();
        String prefix = rockerProperties.getPrefix();
        String suffix = rockerProperties.getSuffix();
        String templatePath = "";

        if(prefix.startsWith("classpath:")){
            prefix = prefix.substring("classpath:".length()+1);
        }
        int pos = templateDirectory.lastIndexOf(prefix);
        if(pos!=-1){
            templateDirectory = templateDirectory.substring(0, pos);
        }

        if(!templateDirectory.endsWith("/")){
            templateDirectory+="/";
        }
        if(prefix.startsWith("/")){
            prefix=prefix.substring(1);
        }
        if(!prefix.endsWith("/")){
            prefix+="/";
        }

        templatePath = templateDirectory + prefix + viewName + locale + suffix;
        Resource resource = applicationContext.getResource(templatePath);
        if(resource==null || !resource.exists()) {
            resource = fileResourceLoader.getResource(templatePath);
        }
        if (resource == null || !resource.exists()) {
            if (locale.isEmpty()) {
                return null;
            }
            int index = locale.lastIndexOf("_");
            return resolveFromLocale(viewName, locale.substring(0, index), applicationContext, rockerProperties);
        }
        return resource;
    }
}
