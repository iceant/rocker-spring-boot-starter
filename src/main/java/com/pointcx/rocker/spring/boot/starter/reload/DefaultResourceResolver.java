package com.pointcx.rocker.spring.boot.starter.reload;

import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import com.pointcx.rocker.spring.boot.starter.util.RockerInternalUtil;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import java.util.Locale;

public class DefaultResourceResolver implements ResourceResolver {
    private final ApplicationContext applicationContext;
    private final RockerProperties properties;

    public DefaultResourceResolver(ApplicationContext applicationContext, RockerProperties properties) {
        this.applicationContext = applicationContext;
        this.properties = properties;
    }

    @Override
    public Resource resolve(String resourceUrlOrName, Locale locale) {
        return RockerInternalUtil.resolveResource(resourceUrlOrName, locale, applicationContext, properties);
    }
}
