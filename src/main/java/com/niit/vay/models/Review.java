package com.niit.vay.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Review must have a reviewer")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private DaoUser user;

    @NotNull(message = "Review must have a product")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productId", referencedColumnName = "productId")
    private Product product;

    @Column
    @NotNull(message = "Review title cannot be null")
    private String title;

    @Column
    @NotNull(message = "Review description cannot be null")
    private String description;

    @Column
    private int rate;

    public Review(DaoUser user, Product product, String title, String description, int rate) {
        this.user = user;
        this.product = product;
        this.title = title;
        this.description = description;
        this.rate = rate;
    }

    public Review() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public DaoUser getUser() {
        return user;
    }

    public void setUser(DaoUser user) {
        this.user = user;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getRate() {
        return rate;
    }

    public void setRate(int rate) {
        this.rate = rate;
    }
}
