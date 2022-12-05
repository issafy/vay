package com.niit.vay.mapper;

import com.niit.vay.dto.LineItemDto;
import com.niit.vay.models.Cart;
import com.niit.vay.models.LineItem;
import com.niit.vay.models.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;

@Component
@Mapper
public interface LineItemMapper {

    LineItemMapper INSTANCE = Mappers.getMapper(LineItemMapper.class);

    @Mapping(target = "lineItemId", ignore = true)
    @Mapping(target = "quantity", source = "lineItemDto.quantity")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "cart", source = "cart")
    LineItem map(LineItemDto lineItemDto, Product product, Cart cart);

    @Mapping(target = "productId", expression = "java(lineItem.getProduct().getProductId())")
    @Mapping(target = "cartId", expression = "java(lineItem.getCart().getCartId())")
    LineItemDto mapToDto(LineItem lineItem);

}
