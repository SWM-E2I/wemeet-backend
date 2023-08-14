package com.e2i.wemeet.config.log;

import com.e2i.wemeet.config.log.aspect.ControllerLogAspect;
import com.e2i.wemeet.config.log.aspect.QueryCounterAspect;
import com.e2i.wemeet.config.log.aspect.ServiceLogAspect;
import com.e2i.wemeet.config.log.module.QueryCounter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.annotation.RequestScope;

@Configuration
public class LogConfig {

    @RequestScope
    @Bean
    public StopWatch apiWatch() {
        return new StopWatch();
    }

    @RequestScope
    @Bean
    public QueryCounter queryCounter() {
        return new QueryCounter();
    }

    @Bean
    public QueryCounterAspect queryCounterAspect() {
        return new QueryCounterAspect(queryCounter());
    }

    @Bean
    public ControllerLogAspect logControllerAspect() {
        return new ControllerLogAspect();
    }

    @Bean
    public ServiceLogAspect logServiceAspect() {
        return new ServiceLogAspect();
    }

}
