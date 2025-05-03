package com.rookiesvnclient.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/svn")
public class SampleController {

    @GetMapping("/test")
    public String test() {
        System.out.println("인증된 사용자만 볼 수 있는 데이터입니다.");
        return "인증된 사용자만 볼 수 있는 데이터입니다.";
    }
}
