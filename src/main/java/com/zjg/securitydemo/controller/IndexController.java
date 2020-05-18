package com.zjg.securitydemo.controller;

import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author zjg
 * @create 2020-03-12 11:41
 */
@RestController
public class IndexController {
    @GetMapping("/hello")
    public String hello() {
        return "security hello";
    }



}
