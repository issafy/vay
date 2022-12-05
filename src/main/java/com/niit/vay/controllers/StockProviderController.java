package com.niit.vay.controllers;

import com.niit.vay.dto.StockProviderDto;
import com.niit.vay.models.StockProvider;
import com.niit.vay.services.StockProviderService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/inventory/stock-providers")
public class StockProviderController {

    private StockProviderService stockProviderService;

    public StockProviderController(StockProviderService stockProviderService) {
        this.stockProviderService = stockProviderService;
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void registerStockProvider(StockProviderDto stockProviderDto, HttpServletResponse response)  throws IOException {
        stockProviderService.save(stockProviderDto);
        response.sendRedirect("/admin/inventory/providers");
    }

    @GetMapping("/list")
    public List<StockProvider> getStockProviders() {
        return stockProviderService.getStockProviders();
    }

    @RequestMapping(value = "/update/{providerId}", method = RequestMethod.POST)
    public void updateStockProvider(@PathVariable Long providerId, StockProviderDto stockProviderDto, HttpServletResponse response) throws IOException {
        if (providerId == 1){
            response.sendError(2, "Cannot modify or edit unlisted provider, category or super category!");
            response.sendRedirect("/admin/inventory/providers");
        }
        stockProviderDto.setId(providerId);
        stockProviderService.update(stockProviderDto);
        response.sendRedirect("/admin/inventory/providers");
    }

    @RequestMapping(value = "/delete/{providerId}", method = RequestMethod.GET)
    public void deleteStockProvider(@PathVariable Long providerId, HttpServletResponse response) throws IOException {
        if (providerId == 1){
            response.sendError(2, "Cannot modify or edit unlisted provider, category or super category!");
            response.sendRedirect("/admin/inventory/providers");
        }
        stockProviderService.deleteStockProvider(providerId);
        response.sendRedirect("/admin/inventory/providers");
    }
}
