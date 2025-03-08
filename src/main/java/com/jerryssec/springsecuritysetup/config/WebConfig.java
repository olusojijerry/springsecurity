package com.jerryssec.springsecuritysetup.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    /*here is where you add all your static pages*/
    public void addViewControllers(ViewControllerRegistry registry){
        registry.addViewController("").setViewName("");
    }
}
