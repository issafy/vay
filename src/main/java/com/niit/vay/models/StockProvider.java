package com.niit.vay.models;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
public class StockProvider {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @NotNull(message = "Stock Provider name cannot be null!")
    @Column
    private String name;

    @NotNull(message = "Stock Provider location cannot be null!")
    @Column
    private String location;

    @NotNull(message = "Stock Provider mail address cannot be null!")
    @Column
    private String email;

    @Column
    private String telephone;

    public StockProvider() {
    }

    public StockProvider(String name, String location, String email, String telephone) {
        this.name = name;
        this.location = location;
        this.email = email;
        this.telephone = telephone;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }
}
