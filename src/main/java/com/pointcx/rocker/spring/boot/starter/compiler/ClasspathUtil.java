package com.pointcx.rocker.spring.boot.starter.compiler;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClasspathUtil {
    public static List<URL> scanClasspath(ClassLoader classLoader) throws IllegalAccessException {
        List<URL> result = new ArrayList<>();
        scanUcpClasspath(classLoader, result);
        return result;
    }

    public static void scanUcpClasspath(ClassLoader classLoader, List<URL> result) throws IllegalAccessException {
        Class classLoaderClass = classLoader.getClass();
        Field ucpField = null;
        try {
            ucpField = classLoaderClass.getDeclaredField("ucp");
            ucpField.setAccessible(true);
        } catch (NoSuchFieldException e) {
        }
        if (ucpField != null) {
            Object ucp = ucpField.get(classLoader);
            Class ucpClass = ucp.getClass();
            Method getURLsMethod = null;
            try {
                getURLsMethod = ucpClass.getDeclaredMethod("getURLs");
                getURLsMethod.setAccessible(true);
            } catch (NoSuchMethodException e) {
            }
            if (getURLsMethod != null) {
                URL[] urls = new URL[0];
                try {
                    urls = (URL[]) getURLsMethod.invoke(ucp);
                } catch (InvocationTargetException e) {
                }
                if (urls != null) {
                    for (URL url : urls) {
                        result.add(url);
                    }
                }
            }
        }

        ClassLoader parent = classLoader.getParent();
        if (parent != null) {
            scanUcpClasspath(parent, result);
        }
    }

}
