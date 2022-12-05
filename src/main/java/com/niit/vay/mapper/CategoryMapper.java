package com.niit.vay.mapper;

import com.niit.vay.dto.CategoryDto;
import com.niit.vay.models.Category;
import com.niit.vay.models.SuperCategory;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Mapping;
import org.springframework.stereotype.Component;


@Component
@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface CategoryMapper {

    CategoryMapper INSTANCE = Mappers.getMapper(CategoryMapper.class);

    @Mapping(target = "categoryId", ignore = true)
    @Mapping(target = "categoryName", source = "categoryDto.name")
    @Mapping(target = "description", source = "categoryDto.description")
    @Mapping(target = "superCategory", source = "superCategory")
    Category map(CategoryDto categoryDto, SuperCategory superCategory);

    @Mapping(target = "superCategoryId", expression = "java(category.getSuperCategory().getSuperCategoryId())")
    CategoryDto mapToDto(Category category);
}
