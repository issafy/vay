package com.niit.vay.services;

import com.niit.vay.dto.LineItemDto;
import com.niit.vay.exceptions.CartNotFoundException;
import com.niit.vay.exceptions.ProductNotFoundException;
import com.niit.vay.mapper.LineItemMapper;
import com.niit.vay.models.Cart;
import com.niit.vay.models.LineItem;
import com.niit.vay.models.Product;
import com.niit.vay.repositories.CartRepository;
import com.niit.vay.repositories.LineItemRepository;
import com.niit.vay.repositories.ProductRepository;
import org.springframework.stereotype.Service;

@Service
public class LineItemService {

    private final LineItemRepository lineItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private LineItemMapper lineItemMapper;

    public LineItemService(LineItemRepository lineItemRepository, CartRepository cartRepository, ProductRepository productRepository, LineItemMapper lineItemMapper) {
        this.lineItemRepository = lineItemRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.lineItemMapper = lineItemMapper;
    }

    public void save(LineItemDto lineItemDto) {
        Product product = productRepository.findById(lineItemDto.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(lineItemDto.getProductId() + ""));
        Cart cart = cartRepository.findById(lineItemDto.getCartId())
                .orElseThrow(() -> new CartNotFoundException(lineItemDto.getCartId() + ""));
        LineItem lineItem = lineItemMapper.map(lineItemDto, product, cart);
        lineItemRepository.save(lineItem);
    }

    public Integer getProductSalesAmount(Product product) {
        Integer amount = 0;
        for(LineItem lineItem: lineItemRepository.findLineItemByProduct(product))
            amount += lineItem.getQuantity();
        return amount;
    }

    public LineItem findById(long lineItemId) {
        return lineItemRepository.findLineItemByLineItemId(lineItemId);
    }

}
