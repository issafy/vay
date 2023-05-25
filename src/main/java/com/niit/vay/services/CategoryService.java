package com.niit.vay.services;

import com.niit.vay.dto.CategoryDto;
import com.niit.vay.exceptions.CategoryNotFoundException;
import com.niit.vay.exceptions.SuperCategoryNotFoundException;
import com.niit.vay.mapper.CategoryMapper;
import com.niit.vay.models.Category;
import com.niit.vay.models.Product;
import com.niit.vay.models.SuperCategory;
import com.niit.vay.repositories.CategoryRepository;
import com.niit.vay.repositories.ProductRepository;
import com.niit.vay.repositories.SuperCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SuperCategoryRepository superCategoryRepository;
    private final CategoryMapper categoryMapper;
    private final ProductService productService;
    private final ProductRepository productRepository;

    public CategoryService(CategoryRepository categoryRepository, SuperCategoryRepository superCategoryRepository, CategoryMapper categoryMapper, ProductService productService, ProductRepository productRepository) {
        this.categoryRepository = categoryRepository;
        this.superCategoryRepository = superCategoryRepository;
        this.categoryMapper = categoryMapper;
        this.productService = productService;
        this.productRepository = productRepository;
    }

    public void save(CategoryDto categoryDto, MultipartFile file) {
        SuperCategory superCategory = superCategoryRepository.findById(categoryDto.getSuperCategoryId())
                .orElseThrow(() -> new SuperCategoryNotFoundException("SuperCategory not found!"));
        if (categoryRepository.findCategoryByCategoryName(categoryDto.getName()) != null)
            throw new RuntimeException("A category with that name already exists!");
        Category category = categoryMapper.map(categoryDto, superCategory);
        category.setImage("/webapp/admin/assets/img/" + (!file.getOriginalFilename().equals("file_dummy.png")?file.getOriginalFilename():"default_category.png"));
        categoryRepository.save(category);
        //Must add a saving block for duplicated files
    }

    public List<Category> getCategories(boolean showUnlisted) {
        List<Category> categories = categoryRepository.findAll();
        if (showUnlisted)
            return categories;
        categories.remove(0);
        return categories;
    }

    public List<Category> getCategoriesFromSuperCategory(SuperCategory superCategory){
        List<Category> categories = new ArrayList<>();
        this.getCategories(true).forEach(category -> {
            if(category.getSuperCategory() == superCategory)
                categories.add(category);
        });
        return categories;
    }



    public void edit(CategoryDto categoryDto, MultipartFile file) {
        SuperCategory superCategory = superCategoryRepository.findById(categoryDto.getSuperCategoryId())
                .orElseThrow(() -> new SuperCategoryNotFoundException("SuperCategory not found!"));
        Category category = categoryRepository.findById(categoryDto.getId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found!"));

        category.setCategoryName(categoryDto.getName());
        category.setDescription(categoryDto.getDescription());
        category.setSuperCategory(superCategory);
        category.setImage("/webapp/admin/assets/img/" + (file.getOriginalFilename().equals("file_dummy.png") ? "default_category.png" : file.getOriginalFilename()));
        categoryRepository.save(category);
    }

    public void deleteCategory(Long categoryId) {
        List<Product> unlistedProducts = productService.loadProductsByCategory(categoryRepository.findCategoryByCategoryId(categoryId));
        for(Product product: unlistedProducts) {
            product.setCategory(categoryRepository.findCategoryByCategoryId(1L));
            productRepository.save(product);
        }
        categoryRepository.delete(categoryRepository.findCategoryByCategoryId(categoryId));
    }
}
