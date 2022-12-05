package com.niit.vay.controllers;

import com.niit.vay.dto.CartDto;
import com.niit.vay.services.CartService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@RequestMapping("/carts")
public class CartController {

    private CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping("/create")
    public void createCart(@RequestBody CartDto cartDto) {
        cartService.save(cartDto);
    }

    @GetMapping("/addToCart/{cartId}/{productId}")
    public void addToCart(@PathVariable long cartId, @PathVariable long productId, HttpServletResponse response) throws IOException {
        cartService.addMultipleLineItemToCart(cartId, productId, 1);
        response.sendRedirect("/shop");
    }

    @GetMapping("/addOneToCart/{cartId}/{productId}")
    public void addOneToCart(@PathVariable long cartId, @PathVariable long productId, HttpServletResponse response) throws IOException {
        cartService.addMultipleLineItemToCart(cartId, productId, 1);
        response.sendRedirect("/shop");
    }

    @GetMapping("/remOneFromCart/{cartId}/{productId}")
    public void remOneFromCart(@PathVariable long cartId, @PathVariable long productId, HttpServletResponse response) throws Exception {
        cartService.minusFromCart(cartId, productId, 1);
        response.sendRedirect("/shop");
    }

//    To add multiple products on cart in a single product page
    @GetMapping("/addMultToCart/{productId}")
    public void addMultipleToCart(String cartId2, @PathVariable long productId, int quantity, HttpServletResponse response) throws IOException {
        cartService.addMultipleLineItemToCart(Long.parseLong(cartId2), productId, quantity);
        response.sendRedirect("/shop");
    }

    @GetMapping("/removeLineItem/{cartId}/{lineItemId}")
    public void removeLineItemFromCart(@PathVariable long cartId, @PathVariable long lineItemId, HttpServletResponse response) throws Exception {
        cartService.removeLineItemFromCart(cartId, lineItemId);
        response.sendRedirect("/shop");
    }

    @GetMapping("/delete/{cartId}")
    public void removeLineItemFromCart(@PathVariable long cartId, HttpServletResponse response) throws Exception {
        cartService.deleteCart(cartId);
        response.sendRedirect("/shop");
    }

}
