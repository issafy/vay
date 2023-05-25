package com.niit.vay.services;

import com.niit.vay.dto.ProductDto;
import com.niit.vay.exceptions.BrandNotFoundException;
import com.niit.vay.exceptions.CategoryNotFoundException;
import com.niit.vay.exceptions.ProductNotFoundException;
import com.niit.vay.mapper.ProductMapper;
import com.niit.vay.models.*;
import com.niit.vay.repositories.BrandRepository;
import com.niit.vay.repositories.CategoryRepository;
import com.niit.vay.repositories.ProductRepository;
import info.debatty.java.stringsimilarity.JaroWinkler;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final ProductMapper productMapper;


    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository, BrandRepository brandRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.productMapper = productMapper;

    }

//  String productName, String description, double unitPrice, Category category, int stock, String image

    public void save(ProductDto productDto, MultipartFile file) {
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException(productDto.getCategoryId().toString()));
        Brand brand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new BrandNotFoundException(productDto.getBrandId().toString()));
        Product product = productMapper.map(productDto, category, brand);
        productRepository.save(new Product(product.getProductName(), product.getDescription(), product.getUnitPrice(), product.getCategory(), "/webapp/admin/assets/img/" + (!file.getOriginalFilename().equals("file_dummy.png")?file.getOriginalFilename():"default_product.png"), brand));
    }

    public Product getProduct(long id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException("Product does not exist!"));
    }

    public List<Product> getProducts() {
        List<Product> products = new ArrayList();
        productRepository.findAll().forEach(products::add);
        return products;
    }

    public List<Product> getPagedProducts(Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 9);
        List<Product> products = new ArrayList();
        productRepository.findAll(pageable).forEach(products::add);
        return products;
    }


    public List<Product> loadProductsByCategory(Category category) {
        return productRepository.findProductByCategory(category);
    }

    public List<Product> loadProductByName(String name){
        return productRepository.findProductByProductNameContaining(name);
    }

    public List<Product> getTop3FeaturedProducts(){
        return productRepository.findTop3ByOrderByUnitPriceDesc();
    }

    public List<Product> getSearchProducts(String search_query) {
        List<Product> productsList = productRepository.findProductByProductNameContaining(search_query);
        if (productsList.isEmpty())
            productRepository.findAll().forEach(productsList::add);
        Map<Double, Product> productsMap = new HashMap<Double, Product>();

        JaroWinkler jw = new JaroWinkler();
        for(Product prd: productsList) {
            productsMap.put(jw.similarity(search_query, prd.getProductName()), prd);
        };
        Map<Double, Product> productsTree = new TreeMap<>(productsMap);
        productsList = new ArrayList<>();
        for(Double doub: productsTree.keySet())
            productsList.add(productsTree.get(doub));
        Collections.reverse(productsList);
        return productsList;
    }

    public void edit(ProductDto productDto, MultipartFile file) {
        Product product = productRepository.findById(productDto.getId()).orElseThrow(() -> new ProductNotFoundException("Product does not exist!"));
        Category category = categoryRepository.findById(productDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("This category does not exist!"));
        Brand brand = brandRepository.findById(productDto.getBrandId())
                .orElseThrow(() -> new BrandNotFoundException("This brand does not exist!"));
        product.setProductName(productDto.getName());
        product.setDescription(productDto.getDescription());
        product.setImage("/webapp/admin/assets/img/" + (file.getOriginalFilename().equals("file_dummy.png") ? "default_product.png" : file.getOriginalFilename()));
        product.setBrand(brand);
        product.setCategory(category);
        product.setUnitPrice(productDto.getPrice());
        productRepository.save(product);
    }


    public void deleteProduct(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new ProductNotFoundException("Product does not exist!"));
        productRepository.delete(product);
    }

    class SortBySimilarity implements Comparator<Product> {
        @Override
        public int compare(Product a, Product b) {
            JaroWinkler jw = new JaroWinkler();
            if ((int)jw.similarity(a.getProductName(), b.getProductName()) > .7)
                return 0;
            return 1;
        }
    }
}
