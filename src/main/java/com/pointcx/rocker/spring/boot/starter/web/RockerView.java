package com.pointcx.rocker.spring.boot.starter.web;

import com.fizzed.rocker.BindableRockerModel;
import com.fizzed.rocker.RenderingException;
import com.fizzed.rocker.RockerModel;
import com.fizzed.rocker.TemplateBindException;
import com.fizzed.rocker.runtime.OutputStreamOutput;
import com.fizzed.rocker.runtime.RockerBootstrap;
import com.pointcx.rocker.spring.boot.starter.RockerProperties;
import org.springframework.core.io.Resource;
import org.springframework.web.servlet.view.AbstractTemplateView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RockerView extends AbstractTemplateView {

    private String viewName;
    private RockerBootstrap rockerBootstrap;
    private RockerProperties properties;

    public RockerView(String viewName, RockerBootstrap rockerBootstrap, RockerProperties properties) {
        this.viewName = viewName;
        this.rockerBootstrap = rockerBootstrap;
        this.properties = properties;
    }

    @Override
    protected void renderMergedTemplateModel(Map<String, Object> map, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) throws Exception {
        RockerModel model = rockerBootstrap.model(viewName);
        BindableRockerModel bindableRockerModel = new BindableRockerModel(viewName, model.getClass().getCanonicalName(), model);

        // -------------------------------------------------------------------
        // bind all parameter in map to rocker model
        for (Map.Entry<String,Object> entry : map.entrySet()) {
            try {
                bindableRockerModel.bind(entry.getKey(), entry.getValue());
            }catch (TemplateBindException bindException){
                // use set method to bind, if the set method don't exist a TemplateBindException will be thrown.
                // ignore it
            }
        }
        httpServletResponse.setContentType(properties.getContentType());
        OutputStreamOutput output = bindableRockerModel.render((contentType, charsetName) -> {
            try {
                return new OutputStreamOutput(contentType, httpServletResponse.getOutputStream(), charsetName);
            } catch (IOException e) {
                throw new RenderingException(e.getMessage(), e);
            }
        });

    }

}
