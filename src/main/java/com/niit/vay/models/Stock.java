package com.niit.vay.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@Entity
public class Stock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long stockId;

    @NotNull(message = "Stocks must select a product to ship!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @Column
    private Instant shipmentDate;

    @NotNull(message = "Stocks must select a provider!")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "providerId", referencedColumnName = "id")
    private StockProvider stockProvider;

    private Integer quantity;

    public Stock() {
    }

    public Stock(Product product, StockProvider stockProvider, Integer quantity) {
        this.product = product;
        this.shipmentDate = Instant.now();
        this.stockProvider = stockProvider;
        this.quantity = quantity;
    }

    public long getStockId() {
        return stockId;
    }

    public void setStockId(long stockId) {
        this.stockId = stockId;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Instant getShipmentDate() {
        return shipmentDate;
    }

    public void setShipmentDate(Instant shipmentDate) {
        this.shipmentDate = shipmentDate;
    }

    public StockProvider getStockProvider() {
        return stockProvider;
    }

    public void setStockProvider(StockProvider stockProvider) {
        this.stockProvider = stockProvider;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
