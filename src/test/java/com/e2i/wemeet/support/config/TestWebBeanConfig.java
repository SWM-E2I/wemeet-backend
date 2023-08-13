package com.e2i.wemeet.support.config;

import com.e2i.wemeet.config.log.module.QueryCounter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.util.StopWatch;

@TestConfiguration
public class TestWebBeanConfig {

    @Bean
    public StopWatch apiWatch() {
        return new StopWatch();
    }

    @Bean
    public QueryCounter queryCounter() {
        return new QueryCounter();
    }

}
