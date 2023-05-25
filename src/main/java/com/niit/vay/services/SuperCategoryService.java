package com.niit.vay.services;

import com.niit.vay.dto.SuperCategoryDto;
import com.niit.vay.models.Category;
import com.niit.vay.models.SuperCategory;
import com.niit.vay.repositories.CategoryRepository;
import com.niit.vay.repositories.SuperCategoryRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class SuperCategoryService {

    private final SuperCategoryRepository superCategoryRepository;
    private final CategoryService categoryService;
    private final CategoryRepository categoryRepository;

    public SuperCategoryService(SuperCategoryRepository superCategoryRepository, CategoryService categoryService, CategoryRepository categoryRepository) {
        this.superCategoryRepository = superCategoryRepository;
        this.categoryService = categoryService;
        this.categoryRepository = categoryRepository;
    }

    public void save(SuperCategoryDto superCategoryDto, MultipartFile file) {
        List<SuperCategory> superCategories = superCategoryRepository.findAll();
        for (SuperCategory superCat: superCategories)
            if (superCat.getName().equals(superCategoryDto.getName()))
                throw new RuntimeException("SuperCategory already exists!");
        superCategoryRepository.save(new SuperCategory(superCategoryDto.getName(), superCategoryDto.getDescription(), "/webapp/admin/assets/img/" + (!file.getOriginalFilename().equals("file_dummy.png")?file.getOriginalFilename():"default_super_category.png")));
    }

    public void update(SuperCategoryDto superCategoryDto, MultipartFile file) {
        SuperCategory superCategory = superCategoryRepository.findById(superCategoryDto.getId()).orElseThrow(() -> new RuntimeException("SuperCategory does not exist!"));

        superCategory.setName(superCategoryDto.getName());
        superCategory.setDescription(superCategoryDto.getDescription());
        superCategory.setImage("/webapp/admin/assets/img/" + (file.getOriginalFilename().equals("file_dummy.png") ? "default_super_category.png" : file.getOriginalFilename()));
        superCategoryRepository.save(superCategory);
    }

    public List<SuperCategory> getSuperCategories(boolean showUnlisted) {
        List<SuperCategory> superCategories = superCategoryRepository.findAll();
        if (showUnlisted)
            return superCategories;
        superCategories.remove(0);
        return superCategories;
    }

    public void deleteSuperCategory(Long superCategoryId) {
        SuperCategory superCategory = superCategoryRepository.findBySuperCategoryId(superCategoryId);
        if (superCategory == null)
            throw new RuntimeException("Super Category does not exist.");
        List<Category> unlistedCategories = categoryService.getCategoriesFromSuperCategory(superCategory);
        for(Category category: unlistedCategories) {
            category.setSuperCategory(superCategoryRepository.findBySuperCategoryId(1L));
            categoryRepository.save(category);
        }
        superCategoryRepository.delete(superCategory);
    }
}
