package com.niit.vay.services;

import com.niit.vay.dto.ReviewDto;
import com.niit.vay.models.*;
import com.niit.vay.repositories.ReviewRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ProductService productService;
    private final MyUserDetailsService userDetailsService;
    private final ShipOrderService shipOrderService;

    public ReviewService(ReviewRepository reviewRepository, ProductService productService, MyUserDetailsService userDetailsService, ShipOrderService shipOrderService) {
        this.reviewRepository = reviewRepository;
        this.productService = productService;
        this.userDetailsService = userDetailsService;
        this.shipOrderService = shipOrderService;
    }

    public List<Review> findReviewsByProduct(Product product) {
        return reviewRepository.findReviewsByProduct(product);
    }

    public Double getProductReview(Product product) throws ArithmeticException {
        int productReviews = findReviewsByProduct(productService.getProduct(product.getProductId())).size();
        int totalRates = 0;
        for (Review r: findReviewsByProduct(productService.getProduct(product.getProductId()))) {
            totalRates += r.getRate();
        }

        try {
            return (double) (totalRates / productReviews);
        } catch (ArithmeticException e) {
            return 0d;
        }
    }

    public List<Review> findReviewsByDaoUserAndProduct(DaoUser daoUser, Product product) {
        return reviewRepository.findReviewsByUserAndProduct(daoUser, product);
    }

    public void saveReview(ReviewDto reviewDto) {
        DaoUser user = userDetailsService.getUser(reviewDto.getUsername());
        Product product = productService.getProduct(reviewDto.getProductId());
        if ((user == null) || (product == null))
            throw new RuntimeException("Review Cannot be submitted!");
        Review review = new Review();
        review.setProduct(product);
        review.setUser(user);
        review.setTitle(reviewDto.getTitle());
        review.setDescription(reviewDto.getDescription());
        review.setRate(reviewDto.getRate());
        reviewRepository.save(review);
    }

    public boolean canUserReviewProduct(DaoUser daoUser, Product product) {
        Boolean canReview = false;
        List<Cart> userInactiveCarts = shipOrderService.shippedUserCarts(daoUser);
        List<LineItem> inactiveLineItems = new ArrayList<>();
        for(Cart cart : userInactiveCarts)
            inactiveLineItems.addAll(cart.getLineItems());
        for(LineItem lineItem : inactiveLineItems)
            if (product.getProductName() == lineItem.getProduct().getProductName())
                canReview = true;

        if(findReviewsByDaoUserAndProduct(daoUser, product).size() > 0)
            canReview = false;
//        System.out.println("Review Array Size: " + findReviewsByDaoUserAndProduct(daoUser, product).size() + " - canReview: " + canReview);
        return canReview;
    }
}
