package com.pointcx.rocker.spring.boot.starter.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

public class RockerClassLoader extends ClassLoader {
    static private final Logger log = LoggerFactory.getLogger(com.fizzed.rocker.reload.RockerClassLoader.class);

    private final SpringRockerReloadingBootstrap bootstrap;

    public RockerClassLoader(SpringRockerReloadingBootstrap bootstrap, ClassLoader parent) {
        super(parent);
        this.bootstrap = bootstrap;
    }

    public boolean isClassLoaded(String className) {
        return this.findLoadedClass(className) != null;
    }

    @Override
    public Class loadClass(String className) throws ClassNotFoundException {
        // only load classes registered with rocker dynamic bootstrap
        if (!bootstrap.isReloadableClass(className)) {
            return super.loadClass(className);
        }

        // load as though class was a resource
        try {
            // views.index -> views/index
            String resourceName = className.replace(".", "/") + ".class";

            /**
             URL url = null;

             // attempt to load from rocker "compile" directory first
             File recompiledFile = new File(this.bootstrap.getConfiguration().getCompileDirectory(), resourceName);

             if (!recompiledFile.exists()) {
             log.debug("Unable to find class in compileDirectory: {}", recompiledFile);

             // fallback to resource
             url = this.getResource(resourceName);
             } else {

             url = recompiledFile.toURI().toURL();

             }
             */

            URL url = this.getResource(resourceName);

            if (url == null) {
                throw new ClassNotFoundException("Class " + className + " not found");
            }

            log.trace("loading class: " + url);

            URLConnection connection = url.openConnection();

            ByteArrayOutputStream buffer;
            try (InputStream input = connection.getInputStream()) {
                buffer = new ByteArrayOutputStream();
                int data = input.read();
                while (data != -1) {
                    buffer.write(data);
                    data = input.read();
                }
            }

            byte[] classData = buffer.toByteArray();

            return defineClass(className, classData, 0, classData.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(e.getMessage(), e);
        }
    }

}