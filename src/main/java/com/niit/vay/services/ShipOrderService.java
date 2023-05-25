package com.niit.vay.services;

import com.niit.vay.checkout.CheckoutItemDto;
import com.niit.vay.dto.ShipOrderDto;
import com.niit.vay.exceptions.CartNotFoundException;
import com.niit.vay.mapper.ShipOrderMapper;
import com.niit.vay.models.*;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.ShipOrderRepository;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ShipOrderService {

    private final ShipOrderRepository shipOrderRepository;
    private final CartRepository cartRepository;
    private final ShipOrderMapper shipOrderMapper;
    private final CartService cartService;
    private final ProductService productService;
    private final LineItemService lineItemService;
    private final MyUserDetailsService myUserDetailsService;
    private final StockService stockService;

    @Value("${baseURL}")
    private String baseURL;

    @Value("${STRIPE_SECRET_KEY}")
    private String apiKey;

    public ShipOrderService(ShipOrderRepository shipOrderRepository, CartRepository cartRepository, ShipOrderMapper shipOrderMapper, CartService cartService, ProductService productService, LineItemService lineItemService, MyUserDetailsService myUserDetailsService, StockService stockService) {
        this.shipOrderRepository = shipOrderRepository;
        this.cartRepository = cartRepository;
        this.shipOrderMapper = shipOrderMapper;
        this.cartService = cartService;
        this.productService = productService;
        this.lineItemService = lineItemService;
        this.myUserDetailsService = myUserDetailsService;
        this.stockService = stockService;
    }

    public void createShipOrder(ShipOrderDto shipOrderDto) {
        Cart cart = cartRepository.findById(shipOrderDto.getCartId())
                .orElseThrow(() -> new CartNotFoundException(shipOrderDto.getCartId() + ""));
        ShipOrder shipOrder = shipOrderMapper.map(shipOrderDto, cart);
        cart.setActive(false);
        for(LineItem lineItem: cart.getLineItems()) {
            stockService.updateProductStock(lineItem.getProduct(), lineItem.getQuantity());
        }
        cartRepository.save(cart);
        Cart newCart = new Cart(cart.getUser(), true);
        cartRepository.save(newCart);
        shipOrderRepository.save(shipOrder);
    }

//    public List<ShipOrder> getShipOrders() {
//        return shipOrderRepository.findAll();
//    }

    public Long getTotalSales() {
        Long total = 0L;
        for (ShipOrder shipOrder: this.getProcessedShipOrders())
            total += cartService.getCartTotal(shipOrder.getCart().getCartId());
        return total;
    }

    public Long getTotalUserSales(Long userId) {
        DaoUser user = myUserDetailsService.getUserByUserId(userId);
        Long totalUserSales = 0L;
        for(Cart cart: cartRepository.getAllByDaoUserAndActive(user, false))
            totalUserSales = totalUserSales + cartService.getCartTotal(cart.getCartId());
        return totalUserSales;
    }

    public List<Cart> shippedUserCarts(DaoUser daoUser) {

        List<ShipOrder> userOrders = this.getShipOrdersByUser(daoUser.getUsername());
        userOrders.removeIf(shipOrder -> !shipOrder.isProcessed());
        List<Cart> shippedCarts = new ArrayList<>();
        for (ShipOrder shipOrder: userOrders)
            shippedCarts.add(shipOrder.getCart());
        return shippedCarts;
    }

    public Product getProductOfTheMonth() {
        Product product;
        List<Product> products = productService.getProducts();
        HashMap<Integer, Product> prodQuan = new HashMap<>();
        for(Product prod: products)
            prodQuan.put(lineItemService.getProductSalesAmount(prod), prod);
        Iterator<Integer> it = prodQuan.keySet().iterator();
        Map<Integer, Product> map = new HashMap<Integer, Product>();
        TreeMap<Integer, Product> tm = new TreeMap<Integer, Product>(prodQuan);
        return tm.lastEntry().getValue();
    }

    public ShipOrder getShipOrderByCart(Cart cart) {
        return shipOrderRepository.getShipOrderByCart(cart);
    }

    public List<ShipOrder> getProcessedShipOrders() {
        return shipOrderRepository.getShipOrderByProcessed(true);
    }

    public List<ShipOrder> getReportShipOrders() {
        return shipOrderRepository.getShipOrderByProcessed(true);
    }


    public List<ShipOrder> getOrderedProcessedShipOrders(Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        List<ShipOrder> shipOrders = shipOrderRepository.getShipOrderByProcessedOrderByCreatedDesc(true, pageable);
        return shipOrders;
    }

    public List<ShipOrder> getShipOrdersByUser(String username) {
        List<ShipOrder> shipOrders = new ArrayList<>();
        List<Cart> inactiveCarts = cartRepository.getAllByActive(false);
        for(Cart cart1: inactiveCarts) {
            if (cart1.getUser().getUsername() != username)
                inactiveCarts.remove(cart1);
        }
        for(Cart cart: inactiveCarts) {
            ShipOrder shipOrder = this.getShipOrderByCart(cart);
            if (shipOrder != null)
                shipOrders.add(shipOrder);
        }
        return shipOrders;
    }

    SessionCreateParams.LineItem.PriceData createPriceData(CheckoutItemDto checkoutItemDto){
        return SessionCreateParams.LineItem.PriceData.builder()
                .setCurrency("usd")
                .setUnitAmount( ((long) checkoutItemDto.getPrice()) * 100)
                .setProductData(
                        SessionCreateParams.LineItem.PriceData.ProductData.builder()
                        .setName(checkoutItemDto.getProductName())
                        .build()
                )
                .build();
    }

    SessionCreateParams.LineItem createSessionLineItem(CheckoutItemDto checkoutItemDto) {
        return SessionCreateParams.LineItem.builder()
                .setPriceData(createPriceData(checkoutItemDto))
                .setQuantity(Long.parseLong(String.valueOf(checkoutItemDto.getQuantity())))
                .build();
    }

    public Session createSession(List<CheckoutItemDto> checkoutItemDtoList ) throws StripeException{

        String successUrl = baseURL + "shipOrders/payment/success?user_id=" + checkoutItemDtoList.get(0).getUserId() + "&old_cart_id=" + checkoutItemDtoList.get(0).getCartId();
        String failedUrl = baseURL + "shipOrders/payment/failed";

        Stripe.apiKey = apiKey;

        List<SessionCreateParams.LineItem> sessionItemsList = new ArrayList<SessionCreateParams.LineItem>();
        for (CheckoutItemDto checkoutItemDto : checkoutItemDtoList) {
            sessionItemsList.add(createSessionLineItem(checkoutItemDto));
        }

        SessionCreateParams params = SessionCreateParams.builder()
                .addPaymentMethodType(SessionCreateParams.PaymentMethodType.CARD)
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .setCancelUrl(failedUrl)
                .setSuccessUrl(successUrl)
                .addAllLineItem(sessionItemsList)
                .build();
            return Session.create(params);
    }
}
