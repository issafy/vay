package com.niit.vay.controllers;

import com.niit.vay.dto.StockDto;
import com.niit.vay.models.Stock;
import com.niit.vay.services.StockService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/inventory")
public class StockController {

    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public void createStock(StockDto stockDto, HttpServletResponse response) throws IOException {
        stockService.save(stockDto);
        response.sendRedirect("/admin/inventory");
    }

    @GetMapping("/list")
    public List<Stock> getStocks() {
        return stockService.getStocks();
    }

    @RequestMapping(value = "/update/{stockId}", method = RequestMethod.POST)
    public void updateStock(@PathVariable Long stockId, StockDto stockDto, HttpServletResponse response) throws IOException {
        stockDto.setStockId(stockId);
        stockService.updateStock(stockDto);
        response.sendRedirect("/admin/inventory");
    }

    @RequestMapping(value = "/delete/{stockId}", method = RequestMethod.GET)
    public void deleteStock(@PathVariable Long stockId, HttpServletResponse response) throws IOException {
        stockService.deleteStock(stockId);
        response.sendRedirect("/admin/inventory");
    }

}
