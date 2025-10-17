package com.nhnacademy.byeol23backend.controller;

import com.nhnacademy.byeol23backend.dto.TestDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class TestController {
    @GetMapping("/backend")
    public TestDTO test(){
        log.info("back");
        return new TestDTO("test", 111);
    }
}
