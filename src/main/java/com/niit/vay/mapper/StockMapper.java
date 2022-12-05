package com.niit.vay.mapper;

import com.niit.vay.dto.StockDto;
import com.niit.vay.models.Product;
import com.niit.vay.models.Stock;
import com.niit.vay.models.StockProvider;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface StockMapper {

    StockMapper INSTANCE = Mappers.getMapper(StockMapper.class);

    @Mapping(target = "stockId", ignore = true)
    @Mapping(target = "quantity", source = "stockDto.quantity")
    @Mapping(target = "product", source = "product")
    @Mapping(target = "stockProvider", source = "stockProvider")
    Stock map(StockDto stockDto, Product product, StockProvider stockProvider);

    @Mapping(target = "productId", expression = "java(stock.getProduct().getProductId())")
    @Mapping(target = "stockProviderId", expression = "java(stock.getStockProvider().getId())")
    StockDto mapToDto(Stock stock);


}
