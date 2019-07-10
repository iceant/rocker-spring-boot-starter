package com.pointcx.rocker.spring.boot.starter.reload;

import org.springframework.core.io.Resource;

import java.util.Locale;

public interface ResourceResolver {
    Resource resolve(String resourceUrlOrName, Locale locale);
}
