package com.learning.study.test.controller;

import com.learning.study.test.service.TestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private TestService testService;

    @GetMapping(value = "/hello")
    public String hello(String name) {
        return testService.sayHello(name);
    }

    @GetMapping(value = "/circuitBreaker")
    public String circuitBreaker(String name) throws Exception {
        return testService.circuitBreaker(name);
    }


}
