package com.niit.vay.controllers;


import com.niit.vay.dto.SuperCategoryDto;
import com.niit.vay.services.StorageServiceImpl;
import com.niit.vay.services.SuperCategoryService;
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
@RequestMapping(value = "/superCategories")
public class SuperCategoryController {

    private final StorageServiceImpl storageService;
    private final SuperCategoryService superCategoryService;

    public SuperCategoryController(StorageServiceImpl storageService, SuperCategoryService superCategoryService) {
        this.storageService = storageService;
        this.superCategoryService = superCategoryService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createSuperCategory(SuperCategoryDto superCategoryDto, @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
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
        superCategoryService.save(superCategoryDto, file);
        response.sendRedirect("/admin/products");
    }

    @RequestMapping(value = "/edit/{superCategoryId}", method = RequestMethod.POST)
    public void editSuperCategory(@PathVariable Long superCategoryId, SuperCategoryDto superCategoryDto, @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
        if (superCategoryId == 1) {
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
        superCategoryDto.setId(superCategoryId);
        superCategoryService.update(superCategoryDto, file);
        response.sendRedirect("/admin/products");
    }

    @RequestMapping(value = "/delete/{superCategoryId}", method = RequestMethod.GET)
    public void deleteSuperCategory(@PathVariable Long superCategoryId, HttpServletResponse response) throws IOException {
        if (superCategoryId == 1) {
            response.sendError(2, "Cannot modify or edit unlisted provider, category or super category!");
        } else {
            superCategoryService.deleteSuperCategory(superCategoryId);
        }
        response.sendRedirect("/admin/products");
    }

}
