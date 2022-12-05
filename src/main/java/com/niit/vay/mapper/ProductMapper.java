package com.niit.vay.mapper;

import com.niit.vay.dto.ProductDto;
import com.niit.vay.models.Brand;
import com.niit.vay.models.Category;
import com.niit.vay.models.Product;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ProductMapper {

    ProductMapper INSTANCE = Mappers.getMapper(ProductMapper.class);

    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "productName", source = "productDto.name")
    @Mapping(target = "description", source = "productDto.description")
    @Mapping(target = "unitPrice", source = "productDto.price")
    @Mapping(target = "category", source = "category")
    @Mapping(target = "brand", source = "brand")
    Product map(ProductDto productDto, Category category, Brand brand);

    @Mapping(target = "categoryId", expression = "java(product.getCategory().getCategoryId())")
    @Mapping(target = "brandId", expression = "java(product.getBrand().getBrandId())")
    ProductDto mapToDto(Product product);
}
