package com.niit.vay.controllers;

import com.niit.vay.dto.CategoryDto;
import com.niit.vay.services.CategoryService;
import com.niit.vay.services.StorageServiceImpl;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    private final StorageServiceImpl storageService;
    private final CategoryService categoryService;

    public CategoryController(StorageServiceImpl storageService, CategoryService categoryService) {
        this.storageService = storageService;
        this.categoryService = categoryService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createCategory(CategoryDto categoryDto, @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException{
        if (file.getOriginalFilename().equals("")) {
            Path path = Paths.get("src/main/webapp/admin/assets/img/file_dummy.png");
            String name = "file_dummy.png";
            String originalFileName = "file_dummy.png";
            String contentType = MediaType.IMAGE_PNG_VALUE;
            byte[] content = null;
            content = Files.readAllBytes(path);
            file = new MockMultipartFile(name, originalFileName, contentType, content);
        }
        storageService.save(file);
        categoryService.save(categoryDto, file);
        response.sendRedirect("/admin/products");
    }

    @RequestMapping(value = "/edit/{categoryId}", method = RequestMethod.POST)
    public void editCategory(@PathVariable Long categoryId, CategoryDto categoryDto, @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        if (categoryId == 1) {
            response.sendError(2, "Cannot modify or edit unlisted provider, category or super category!");
            response.sendRedirect("/admin/products");
        }
        if (file.getOriginalFilename().equals("")) {
            Path path = Paths.get("src/main/webapp/admin/assets/img/file_dummy.png");
            String name = "file_dummy.png";
            String originalFileName = "file_dummy.png";
            String contentType = MediaType.IMAGE_PNG_VALUE;
            byte[] content = null;
            content = Files.readAllBytes(path);
            file = new MockMultipartFile(name, originalFileName, contentType, content);
        }

        storageService.save(file);
        categoryDto.setId(categoryId);
        categoryService.edit(categoryDto, file);
        response.sendRedirect("/admin/products");
    }

    @RequestMapping(value = "/delete/{categoryId}", method = RequestMethod.GET)
    public void deleteCategory(@PathVariable Long categoryId, HttpServletResponse response) throws IOException {

        if (categoryId == 1) {
            response.sendError(2, "Cannot modify or edit unlisted provider, category or super category!");
        } else {
            categoryService.deleteCategory(categoryId);
        }
        response.sendRedirect("/admin/products");

    }
}
