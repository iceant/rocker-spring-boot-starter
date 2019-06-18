package com.pointcx.rocker.spring.boot.starter.util;

import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import org.springframework.beans.propertyeditors.LocaleEditor;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.Locale;

public class RockerInternalUtil {

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
        String templatePath = rockerProperties.getPrefix() + viewName + locale + rockerProperties.getSuffix();
        Resource resource = applicationContext.getResource(templatePath);
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
