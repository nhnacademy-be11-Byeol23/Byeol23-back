package com.nhnacademy.byeol23backend.controller;

import com.nhnacademy.byeol23backend.dto.TestDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/backend")
    public TestDTO test(){
        return new TestDTO("test", 111);
    }
}
