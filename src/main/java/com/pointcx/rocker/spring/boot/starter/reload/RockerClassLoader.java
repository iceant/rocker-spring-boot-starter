package com.pointcx.rocker.spring.boot.starter.reload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class RockerClassLoader extends ClassLoader {
    static private final Logger log = LoggerFactory.getLogger(RockerClassLoader.class);

    private final RockerReloadableBootstrap bootstrap;

    public RockerClassLoader(RockerReloadableBootstrap bootstrap, ClassLoader parent) {
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

        InputStream inputStream = null;
        ByteArrayOutputStream outputStream = null;
        // load as though class was a resource
        try {
            // views.index -> views/index
            String resourceName = className.replace(".", "/") + ".class";

            URL url = this.getResource(resourceName);

            if (url == null) {
                throw new ClassNotFoundException("Class " + className + " not found");
            }

            log.trace("loading class: " + url);

            URLConnection connection = url.openConnection();

            byte[] buffer = new byte[1024];
            int len;

            inputStream = connection.getInputStream();
            outputStream = new ByteArrayOutputStream();
            while((len = inputStream.read(buffer))!=-1){
                outputStream.write(buffer, 0, len);
            }
            outputStream.flush();

            byte[] classData = outputStream.toByteArray();

            return defineClass(className, classData, 0, classData.length);
        } catch (IOException e) {
            throw new ClassNotFoundException(e.getMessage(), e);
        }finally {
            if(inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                }
                inputStream =null;
            }

            if(outputStream!=null){
                try {
                    outputStream.close();
                } catch (IOException e) {
                }
                outputStream = null;
            }
        }
    }

}