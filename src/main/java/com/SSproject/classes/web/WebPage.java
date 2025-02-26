package com.example.SSproject.web;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WebPage {

    @GetMapping("/")
    public String LoginPage(HttpServletRequest httpServletRequest){
        return httpServletRequest.getSession().getId();
    }
}
