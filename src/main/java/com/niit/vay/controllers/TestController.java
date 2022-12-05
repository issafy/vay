package com.niit.vay.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class TestController {

    @GetMapping("/hello")
    public ModelAndView HelloWorld() {
        ModelAndView mdv = new ModelAndView("HelloWorld");
        MultipartFile file = null;
        mdv.addObject("file", file);
        return mdv;
    }

}
