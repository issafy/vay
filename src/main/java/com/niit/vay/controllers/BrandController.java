package com.niit.vay.controllers;

import com.niit.vay.dto.BrandDto;
import com.niit.vay.services.BrandService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/brands")
public class BrandController {

    private BrandService brandService;

    public BrandController(BrandService brandService) {
        this.brandService = brandService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void saveBrand(BrandDto brandDto) {
        brandService.save(brandDto);
    }

}
