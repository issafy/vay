package com.niit.vay.services;

import com.niit.vay.dto.BrandDto;
import com.niit.vay.models.Brand;
import com.niit.vay.repositories.BrandRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandService {

    private final BrandRepository brandRepository;

    public BrandService(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    public void save(BrandDto brandDto) {
        Brand brand = brandRepository.findBrandByName(brandDto.getName());
        if (brand != null)
            throw new RuntimeException("Brand already exists!");
        brand.setBrandImage(brandDto.getBrandImage());
        brandRepository.save(brand);
    }

    public List<Brand> getBrands() {
        return brandRepository.findAll();
    }

}
