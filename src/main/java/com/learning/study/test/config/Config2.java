package com.learning.study.test.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;

import javax.annotation.PostConstruct;

@AutoConfigureBefore(Config1.class)
@Slf4j
public class Config2 {

    @PostConstruct
    public void init() {
        log.info("----Config2----");
    }
}
