package com.niit.vay.services;

import com.niit.vay.dto.StockProviderDto;
import com.niit.vay.exceptions.StockProviderNotFoundException;
import com.niit.vay.models.StockProvider;
import com.niit.vay.repositories.StockProviderRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockProviderService {

    private final StockProviderRepository stockProviderRepository;

    public StockProviderService(StockProviderRepository stockProviderRepository) {
        this.stockProviderRepository = stockProviderRepository;
    }

    public void save(StockProviderDto stockProviderDto) {
        if (stockProviderRepository.findByName(stockProviderDto.getName()) != null && stockProviderRepository.findByEmail(stockProviderDto.getEmail()) != null)
            throw new RuntimeException("Stock Provider already exists!");
        StockProvider stockProvider = new StockProvider();

        stockProvider.setName(stockProviderDto.getName());
        stockProvider.setLocation(stockProviderDto.getLocation());
        stockProvider.setEmail(stockProviderDto.getEmail());
        stockProvider.setTelephone(stockProviderDto.getTelephone());

        stockProviderRepository.save(stockProvider);
    }

    public void update(StockProviderDto stockProviderDto) {
        StockProvider stockProvider = stockProviderRepository.findById(stockProviderDto.getId()).orElseThrow(() -> new StockProviderNotFoundException("This provider does not exist!"));
        stockProvider.setLocation(stockProviderDto.getLocation());
        stockProvider.setName(stockProviderDto.getName());
        stockProvider.setTelephone(stockProviderDto.getTelephone());
        stockProviderRepository.save(stockProvider);
    }

    public List<StockProvider> getStockProviders() {
        List<StockProvider> stockProviders = new ArrayList<>();
        stockProviderRepository.findAll().forEach(stockProviders::add);
        return stockProviders;
    }

    public List<StockProvider> getPagedStockProviders(Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        List<StockProvider> stockProviders = new ArrayList<>();
        stockProviderRepository.findAll(pageable).forEach(stockProviders::add);
        return stockProviders;
    }

    public void deleteStockProvider(Long providerId) {
        StockProvider stockProvider = stockProviderRepository.findById(providerId).orElseThrow(() -> new StockProviderNotFoundException("This provider does not exist!"));
        stockProviderRepository.delete(stockProvider);
    }
}
