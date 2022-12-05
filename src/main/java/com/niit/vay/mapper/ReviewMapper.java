package com.niit.vay.mapper;


import com.niit.vay.dto.ReviewDto;
import com.niit.vay.models.DaoUser;
import com.niit.vay.models.Product;
import com.niit.vay.models.Review;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ReviewMapper {

    ReviewMapper INSTANCE = Mappers.getMapper(ReviewMapper.class);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "title", source = "reviewDto.title")
    @Mapping(target = "description", source = "reviewDto.description")
    @Mapping(target = "rate", source = "reviewDto.rate")
    @Mapping(target = "user", source = "user")
    @Mapping(target = "product", source = "product")
    Review map(ReviewDto reviewDto, DaoUser user, Product product);

    @Mapping(target = "username", expression = "java(review.getUser().getUsername())")
    @Mapping(target = "productId", expression = "java(review.getProduct().getProductId())")
    ReviewDto mapToDto(Review review);
}
