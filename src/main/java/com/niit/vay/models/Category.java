package com.niit.vay.models;

import org.springframework.lang.Nullable;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Entity
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long categoryId;

    @NotBlank(message = "Category name cannot be empty or null")
    private String categoryName;

    @NotNull(message = "Category must have a SuperCategory")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "superCategoryId", referencedColumnName = "superCategoryId")
    private SuperCategory superCategory;

    @NotEmpty(message = "Category Description cannot be empty!")
    private String description;

    @Nullable
    private String image;

    public Category(String categoryName, SuperCategory superCategory, String description, String image) {
        this.categoryName = categoryName;
        this.superCategory = superCategory;
        this.description = description;
        this.image = image;
    }

    public Category() {
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public SuperCategory getSuperCategory() {
        return superCategory;
    }

    public void setSuperCategory(SuperCategory superCategory) {
        this.superCategory = superCategory;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
