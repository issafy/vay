package com.niit.vay.config;

import com.niit.vay.models.Cart;
import com.niit.vay.models.LineItem;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.LineItemRepository;
import com.niit.vay.services.CartService;
import com.niit.vay.services.LineItemService;
import com.niit.vay.services.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Component
public class ScheduledTasks {

    private final CartService cartService;
    private final CartRepository cartRepository;
    private final LineItemService lineItemService;
    private final StockService stockService;
    private final LineItemRepository lineItemRepository;

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);

    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    public ScheduledTasks(CartService cartService, CartRepository cartRepository, LineItemService lineItemService, StockService stockService, LineItemRepository lineItemRepository) {
        this.cartService = cartService;
        this.cartRepository = cartRepository;
        this.lineItemService = lineItemService;
        this.stockService = stockService;
        this.lineItemRepository = lineItemRepository;
    }

    @Scheduled(fixedRate = 60000)
    public void updateCartLineItems() {
        List<Cart> activeCarts = cartRepository.getAllByActive(true);
        List<LineItem> activeLineItems =  new ArrayList<>();
        for(Cart cart : activeCarts)
            activeLineItems.addAll(cart.getLineItems());
        for(LineItem lineItem : activeLineItems) {
            Integer productStock = stockService.getProductStock(lineItem.getProduct());
            if (lineItem.getQuantity() > productStock)
                lineItem.setQuantity(productStock);
            lineItemRepository.save(lineItem);
        }
        log.info("Updating line items at {}", dateFormat.format(new Date()));
    }

}
