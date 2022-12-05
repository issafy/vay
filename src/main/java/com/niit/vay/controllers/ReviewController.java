package com.niit.vay.controllers;

import com.niit.vay.dto.ReviewDto;
import com.niit.vay.services.ReviewService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping(value = "/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @RequestMapping(value = "/create",method = RequestMethod.POST)
    public void createReview(ReviewDto reviewDto, HttpServletResponse response) throws IOException {
        reviewService.saveReview(reviewDto);
        response.sendRedirect("/shop-single/" + reviewDto.getProductId());
    }
}
