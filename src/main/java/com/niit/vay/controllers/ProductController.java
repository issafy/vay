package com.niit.vay.controllers;

import com.niit.vay.dto.ProductDto;
import com.niit.vay.services.ProductService;
import com.niit.vay.services.StorageService;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/products")
public class ProductController {


    private final ProductService productService;
    private final StorageService storageService;

    public ProductController(ProductService productService, StorageService storageService) {
        this.productService = productService;
        this.storageService = storageService;
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/create")
    public void createProduct(ProductDto productDto, @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {
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
        productService.save(productDto, file);
        response.sendRedirect("/admin/products");
    }

    @RolesAllowed("ADMIN")
    @PostMapping("/edit/{productId}")
    public void editProduct(@PathVariable Long productId, ProductDto productDto, @RequestParam("file") MultipartFile file, HttpServletResponse response) throws IOException {

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
        productDto.setId(productId);
        productService.edit(productDto, file);
        response.sendRedirect("/admin/products");
    }

    @RequestMapping(value = "/delete/{productId}", method = RequestMethod.GET)
    public void deleteProduct(@PathVariable Long productId, HttpServletResponse response) throws IOException {
        productService.deleteProduct(productId);
        response.sendRedirect("/admin/products");
    }

    @RolesAllowed({"USER", "ADMIN"})
    @GetMapping("/{id}")
    public String getProduct(@PathVariable long id) {
        return productService.getProduct(id).toString();
    }


}
