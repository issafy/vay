package com.niit.vay.mapper;

import com.niit.vay.dto.CartDto;
import com.niit.vay.models.Cart;
import com.niit.vay.models.DaoUser;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CartMapper {

    CartMapper INSTANCE  = Mappers.getMapper(CartMapper.class);

    @Mapping(target = "cartId", ignore = true)
    @Mapping(target = "active", source = "cartDto.active")
    @Mapping(target = "user", source = "daoUser")
//    was here... daoUser instead of user
    Cart map(CartDto cartDto, DaoUser daoUser);

    @Mapping(target = "userId", expression = "java(cart.getUser().getUserId())")
    CartDto mapToDto(Cart cart);

}
