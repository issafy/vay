package com.niit.vay.services;

import com.niit.vay.dto.StockDto;
import com.niit.vay.exceptions.ProductNotFoundException;
import com.niit.vay.exceptions.StockNotFoundException;
import com.niit.vay.exceptions.StockProviderNotFoundException;
import com.niit.vay.mapper.StockMapper;
import com.niit.vay.models.Product;
import com.niit.vay.models.Stock;
import com.niit.vay.models.StockProvider;
import com.niit.vay.repositories.ProductRepository;
import com.niit.vay.repositories.StockProviderRepository;
import com.niit.vay.repositories.StockRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockProviderRepository stockProviderRepository;
    private final ProductRepository productRepository;
    private final StockMapper stockMapper;

    public StockService(
            StockRepository stockRepository,
            StockProviderRepository stockProviderRepository,
            StockMapper stockMapper,
            ProductRepository productRepository
            ) {
        this.stockRepository = stockRepository;
        this.stockProviderRepository = stockProviderRepository;
        this.stockMapper = stockMapper;
        this.productRepository = productRepository;
    }

    public void save(StockDto stockDto) {
        StockProvider stockProvider = stockProviderRepository.findById(stockDto.getStockProviderId())
                .orElseThrow(() -> new RuntimeException("Stock Provider does not exist!"));
        Product product = productRepository.findById(stockDto.getProductId())
                .orElseThrow(() -> new RuntimeException("Product does not exist!"));
        Stock stock = stockMapper.map(stockDto, product, stockProvider);
        stock.setShipmentDate(Instant.now());
        stockRepository.save(stock);

    }

    public List<Stock> getStocks() {
        List<Stock> stocks = new ArrayList<>();
        stockRepository.findAll().forEach(stocks::add);
        return stocks;
    }

    public List<Stock> getPagedStocks(Optional<Integer> page) {
        Pageable pageable = PageRequest.of(page.orElse(0), 10);
        List stocks = new ArrayList();
        stockRepository.findAll(pageable).forEach(stocks::add);
        return stocks;
    }

    public void updateStock(StockDto stockDto) {
        Stock stock = stockRepository.findById(stockDto.getStockId()).orElseThrow(() -> new StockNotFoundException("This stock does not exist!"));
        Product product = productRepository.findById(stockDto.getProductId()).orElseThrow(() -> new ProductNotFoundException("This product does not exist!"));
        StockProvider stockProvider = stockProviderRepository.findById(stockDto.getStockProviderId()).orElseThrow(() -> new StockProviderNotFoundException("This stock provider does not exist!"));
        stock.setProduct(product);
        stock.setStockProvider(stockProvider);
        stock.setQuantity(stockDto.getQuantity());
        stock.setShipmentDate(Instant.now());
        stockRepository.save(stock);
    }

    public void updateProductStock(Product product, Integer quantity) {
        List<Stock> productStocks = stockRepository.findAllByProduct(product);

        while(quantity > 0)
            for(Stock stock: productStocks) {
                if (stock.getQuantity() > 0) {
                    stock.setQuantity(stock.getQuantity() - 1);
                    stockRepository.save(stock);
                    quantity -= 1;
                }
            }
    }

    public Integer getProductStock(Product product) {
        Integer stock = 0;
        List<Stock> productStocks = stockRepository.findAllByProduct(product);
        if (productStocks.size() == 0)
            return null;
        for(Stock stck : productStocks)
            stock += stck.getQuantity();
        return stock;
    }

    public String getProductStockStatus(Product product) {
        Integer productStock = this.getProductStock(product);
        if (productStock == null)
            return "Not available at the moment.";
        else if(productStock == 0)
            return "Out of stock.";
        else
            return productStock + " left in stock.";
    }

    public void deleteStock(Long stockId) {
        Stock stock = stockRepository.findById(stockId).orElseThrow(() -> new StockNotFoundException("This stock does not exist!"));
        if (stock.getStockProvider().getName().equals("Unlisted Provider"))
            stockRepository.delete(stock);
        stock.setStockProvider(stockProviderRepository.getStockProviderById(1L));
        stockRepository.save(stock);
    }
}
