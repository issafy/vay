package com.niit.vay.controllers;

import com.niit.vay.services.VayJdbcService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/api/")
public class CustomRestController {

    private final VayJdbcService vayJdbcService;

    public CustomRestController(VayJdbcService vayJdbcService) {
        this.vayJdbcService = vayJdbcService;
    }

//    @RequestMapping(value = "search", method = RequestMethod.GET)
//    public List<String> search()

}
