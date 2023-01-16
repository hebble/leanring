package com.learning.study.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
@Slf4j
public class Config1 {

    @PostConstruct
    public void init() {
        log.info("----Config1----");
    }
}
