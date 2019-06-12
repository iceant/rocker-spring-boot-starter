package com.pointcx.rocker.spring.boot.starter;

import com.fizzed.rocker.runtime.RockerBootstrap;
import com.pointcx.rocker.spring.boot.starter.reload.SpringRockerDefaultBootstrap;
import com.pointcx.rocker.spring.boot.starter.reload.SpringRockerReloadingBootstrap;
import com.pointcx.rocker.spring.boot.starter.web.RockerViewResolver;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rocker.$r;

@Configuration
@ConditionalOnClass(RockerBootstrap.class)
@ConditionalOnProperty(prefix = "spring.rocker", name = {"enabled"}, havingValue = "true")
@EnableConfigurationProperties(RockerProperties.class)
public class RockerAutoConfiguration {
    final RockerProperties properties;

    public RockerAutoConfiguration(RockerProperties properties) {
        this.properties = properties;
    }

    @Bean
    @ConditionalOnMissingBean(RockerBootstrap.class)
    public RockerBootstrap rockerBootstrap(){
        if(properties.isReloading()){
            return new SpringRockerReloadingBootstrap(properties);
        }else{
            return new SpringRockerDefaultBootstrap(properties);
        }
    }

    @Bean
    @ConditionalOnMissingBean(RockerViewResolver.class)
    public RockerViewResolver rockerViewResolver(RockerBootstrap rockerBootstrap){
        RockerViewResolver viewResolver = new RockerViewResolver(rockerBootstrap, properties);
        viewResolver.setPrefix(properties.getPrefix());
        viewResolver.setSuffix(properties.getSuffix());
        viewResolver.setOrder(properties.getTemplateResolverOrder());
        return viewResolver;
    }

    @Bean
    @ConditionalOnMissingBean(SpringRocker.class)
    public SpringRocker rocker(RockerBootstrap rockerBootstrap){
        return new SpringRocker(rockerBootstrap);
    }

    @Bean
    @ConditionalOnMissingBean($r.class)
    public $r rockerUtil(ApplicationContext applicationContext){
        $r rocker = new $r();
        rocker.setApplicationContext(applicationContext);
        return rocker;
    }

}

