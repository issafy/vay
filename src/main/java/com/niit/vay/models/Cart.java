package com.niit.vay.models;

import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;

import java.io.Serializable;
import java.time.Instant;
import java.util.List;

@Entity
public class Cart implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long cartId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", referencedColumnName = "userId")
    private DaoUser daoUser;

    private Instant created;

    @OneToMany(fetch = FetchType.EAGER)
    private List<LineItem> lineItems;

    private boolean active = true;

    public Cart(DaoUser daoUser, boolean active) {
        this.daoUser = daoUser;
        this.created = Instant.now();
        this.active = active;
    }

    public Cart() {
    }

    @Transactional
    public List<LineItem> getLineItems() {
        return lineItems;
    }

    public void setLineItems(List<LineItem> lineItems) {
        this.lineItems = lineItems;
    }

    public long getCartId() {
        return cartId;
    }

    public void setCartId(long cartId) {
        this.cartId = cartId;
    }

    @Transactional
    public DaoUser getUser() {
        return daoUser;
    }

    public void setUser(DaoUser daoUser) {
        this.daoUser = daoUser;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public boolean getActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

}
