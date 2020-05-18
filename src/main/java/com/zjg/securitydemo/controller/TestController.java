package com.zjg.securitydemo.controller;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zjg
 * @create 2020-03-12 18:47
 */
@RestController
public class TestController {
    @GetMapping("/index")
    public Object index() {
        /*
            获取到Authentication对象信息
            除了通过这种方式获取Authentication对象信息外，也可以使用下面这种方式:
            public Object index(Authentication authentication) {
         */
        return SecurityContextHolder.getContext().getAuthentication();
    }
}
