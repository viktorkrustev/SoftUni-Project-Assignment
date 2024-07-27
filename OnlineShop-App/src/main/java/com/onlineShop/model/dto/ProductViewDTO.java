package com.onlineshop.model.dto;

import java.util.List;

public class ProductViewDTO {
    private Long id;
    private String name;
    private double price;
    private String brand;
    private String description;
    private String imageUrl;
    private List<ReviewViewDTO> reviews;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public List<ReviewViewDTO> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewViewDTO> reviews) {
        this.reviews = reviews;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}