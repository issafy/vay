package com.niit.vay.controllers;

import com.niit.vay.checkout.CheckoutItemDto;
import com.niit.vay.checkout.StripeResponse;
import com.niit.vay.dto.ShipOrderDto;
import com.niit.vay.models.*;
import com.niit.vay.repositories.ShipOrderRepository;
import com.niit.vay.services.CartService;
import com.niit.vay.services.MyUserDetailsService;
import com.niit.vay.services.ShipOrderService;
import com.stripe.exception.StripeException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import com.stripe.model.checkout.Session;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.thymeleaf.TemplateEngine;

import javax.annotation.security.RolesAllowed;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/shipOrders")
public class ShipOrderController {

    private final ShipOrderService shipOrderService;
    private final CartService cartService;
    private final MyUserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final ShipOrderRepository shipOrderRepository;

    @Autowired
    TemplateEngine templateEngine;
    @Autowired
    ServletContext servletContext;

    @Value("${STRIPE_PUBLIC_KEY}")
    private String stripePublicKey;

    public ShipOrderController(CartService cartService, MyUserDetailsService userDetailsService, AuthenticationManager authenticationManager, ShipOrderService shipOrderService, ShipOrderRepository shipOrderRepository) {
        this.cartService = cartService;
        this.userDetailsService = userDetailsService;
        this.authenticationManager = authenticationManager;
        this.shipOrderService = shipOrderService;
        this.shipOrderRepository = shipOrderRepository;
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @PostMapping("/create")
    public void createShipOrder(@RequestBody ShipOrderDto shipOrderDto) {
        shipOrderService.createShipOrder(shipOrderDto);
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping("/checkout")
    public String checkout(Model model) {
        model.addAttribute("amount", 50 * 100); // in cents
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", ChargeRequest.Currency.EUR);
        return "/template/checkout";
    }

//    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/create-checkout-session/", method = RequestMethod.POST)
    public ResponseEntity<StripeResponse> checkoutList(String cartId3, HttpServletResponse response, HttpSession httpSession) throws StripeException, IOException {
        Cart cart = cartService.getCartbyCartId(Long.parseLong(cartId3));
        List<CheckoutItemDto> checkoutItemDtoList = new ArrayList<>();
        for(LineItem lineitem : cart.getLineItems())
            checkoutItemDtoList.add(new CheckoutItemDto(
                    lineitem.getProduct().getProductName(),
                    lineitem.getQuantity(),
                    lineitem.getProduct().getUnitPrice(),
                    lineitem.getProduct().getProductId(),
                    cart.getUser().getUserId(),
                    cart.getCartId()));
        Session session = shipOrderService.createSession(checkoutItemDtoList);
        StripeResponse stripeResponse = new StripeResponse(session.getId());
        ShipOrder shipOrder = shipOrderService.getShipOrderByCart(cart);
        shipOrder.setProcessed(true);
        shipOrderRepository.save(shipOrder);
        httpSession.setAttribute("oldCart", cart);
        session.setSuccessUrl(session.getSuccessUrl() + "&old_cart_id=" + cart.getCartId());
        response.sendRedirect(session.getUrl());
        return new ResponseEntity<StripeResponse>(stripeResponse, HttpStatus.OK);
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/order-cart/", method = RequestMethod.POST)
    public void orderCart(String cartId3, HttpServletResponse response, HttpServletRequest request) throws StripeException, IOException {
        Cart cart = cartService.getCartbyCartId(Long.parseLong(cartId3));
        shipOrderService.createShipOrder(new ShipOrderDto(1, cart.getCartId(), LocalDateTime.now().plusMonths(1)));
        request.getSession().setAttribute("oldCart", cart);
        response.sendRedirect("/account/" + cart.getUser().getUsername());
    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/payment/success", method = RequestMethod.GET)
    public void success(String user_id, String old_cart_id, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {

        System.out.println("UserId from stripe: " + user_id);
        DaoUser daoUser = userDetailsService.getUserByUserId(Long.valueOf(user_id));

        Cart oldCart = cartService.getCartbyCartId(Long.valueOf(old_cart_id));

        response.sendRedirect("/payment/invoice/generated?user_id=" + user_id + "&old_cart_id=" + oldCart.getCartId());


    }

    @RolesAllowed({"ROLE_ADMIN", "ROLE_USER"})
    @RequestMapping(value = "/payment/failed", method = RequestMethod.GET)
    public ModelAndView failure(String user_id, String old_cart_id, HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {

        ModelAndView mav = new ModelAndView("/payment/failed");
        System.out.println("UserId from stripe: " + user_id);
        DaoUser daoUser = userDetailsService.getUserByUserId(Long.valueOf(user_id));

        Cart oldCart = cartService.getCartbyCartId(Long.valueOf(old_cart_id));

        return mav;


    }


}
