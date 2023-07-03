package com.e2i.wemeet.config.docs;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Resource Handler 설정
@Configuration
public class StaticRoutingConfiguration implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/dist/**").addResourceLocations("classpath:/static/dist/");
        registry.addResourceHandler("swagger-ui.html").addResourceLocations("classpath:/static/dist/swagger-ui/");
    }
}
