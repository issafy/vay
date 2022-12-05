package com.niit.vay.models;

import javax.persistence.*;
import java.time.Instant;
import java.time.LocalDateTime;

@Entity
public class ShipOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long shipOrderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cartId", referencedColumnName = "cartId")
    private Cart cart;

    private LocalDateTime deliveryDate;

    @Column
    private boolean processed;

    @Transient
    private long total;

    private Instant created;

    public ShipOrder(Cart cart, LocalDateTime deliveryDate) {
        this.cart = cart;
        this.deliveryDate = deliveryDate;
        this.created = Instant.now();
        this.processed = false;

        long orderTotal = 0;
        for (LineItem li: cart.getLineItems())
            orderTotal = orderTotal + li.getTotal();
    }

    public ShipOrder() {
    }

    public long getShipOrderId() {
        return shipOrderId;
    }

    public void setShipOrderId(long shipOrderId) {
        this.shipOrderId = shipOrderId;
    }

    public Cart getCart() {
        return cart;
    }

    public void setCart(Cart cart) {
        this.cart = cart;
    }

    public LocalDateTime getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(LocalDateTime deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public boolean isProcessed() {
        return processed;
    }

    public void setProcessed(boolean processed) {
        this.processed = processed;
    }
}
