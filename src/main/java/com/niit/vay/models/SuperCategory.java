package com.niit.vay.models;

import org.sonatype.inject.Nullable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotBlank;

@Entity
public class SuperCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long superCategoryId;

    @NotBlank(message = "SuperCategory name cannot be empty or null!")
    private String name;

    @NotBlank(message = "SuperCategory Description cannot be empty!")
    private String description;

    @Nullable
    private String image;

    public SuperCategory(String name, String description, String image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    public SuperCategory() {
    }

    public long getSuperCategoryId() {
        return superCategoryId;
    }

    public void setSuperCategoryId(long superCategoryId) {
        this.superCategoryId = superCategoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
