package com.pointcx.rocker.spring.boot.starter.compiler;

import sun.misc.URLClassPath;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClasspathUtil {
    public static List<URL> scanClasspath(ClassLoader classLoader) throws IllegalAccessException {
        List<URL> result =  new ArrayList<>();
        scanUcpClasspath(classLoader, result);
        return result;
    }

    public static void scanUcpClasspath(ClassLoader classLoader, List<URL> result) throws IllegalAccessException {
        Class classLoaderClass = classLoader.getClass();
        try {
            Field field = classLoaderClass.getDeclaredField("ucp");
            field.setAccessible(true);
            Object ucp = field.get(classLoader);
            if(ucp instanceof URLClassPath){
                URLClassPath urlClassPath = (URLClassPath)ucp;
                URL[] paths = urlClassPath.getURLs();
                for(URL url : paths){
                    result.add(url);
                }
            }
        } catch (NoSuchFieldException e) {
        }

        ClassLoader parent = classLoader.getParent();
        if(parent!=null) {
            scanUcpClasspath(parent, result);
        }

    }


}
