package com.niit.vay.controllers;

import com.niit.vay.services.*;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Controller
public class MyErrorController implements ErrorController {

    private final CartService cartService;
    private final BrandService brandService;
    private final MyUserDetailsService userDetailsService;
    private final CategoryService categoryService;
    private final VayJdbcService vayJdbcService;
    private final AuthController authController;

    public MyErrorController(CartService cartService, BrandService brandService, MyUserDetailsService userDetailsService, CategoryService categoryService, VayJdbcService vayJdbcService, AuthController authController) {
        this.cartService = cartService;
        this.brandService = brandService;
        this.userDetailsService = userDetailsService;
        this.categoryService = categoryService;
        this.vayJdbcService = vayJdbcService;
        this.authController = authController;
    }

    @RequestMapping("/error")
    public ModelAndView getError(HttpSession session, HttpServletRequest request) throws IOException {
        ModelAndView mav = new ModelAndView("error");
        authController.setSessionUser(session, request);
        mav.addObject("httpSession", session);
        mav.addObject("cartService", cartService);
        mav.addObject("userDetailsService", userDetailsService);
        mav.addObject("brandService", brandService);
        mav.addObject("categoryService", categoryService);
        mav.addObject("vayJdbcService", vayJdbcService);

        return mav;
    }

}
