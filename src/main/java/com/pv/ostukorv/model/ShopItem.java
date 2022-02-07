package com.pv.ostukorv.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class ShopItem {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Integer id;
    private String name;
    private String description;
    private double price;
    String type;
    String category;
    Integer count;
    String imgUrl;

    public Integer getId() {
        return this.id;
    }
    public String getName() {
        return this.name;
    }
    public String getDescription() {
        return this.description;
    }
    public double getPrice() {
        return this.price;
    }
    public String getType() {
        return type;
    }
    public String getCategory() {
        return category;
    }
    public Integer getCount() {
        return count;
    }
    public String getImgUrl() {
        return imgUrl;
    }

    public void setId(Integer id) {
        this.id = id;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public void setType(String type) {
        this.type = type;
    }
    public void setCategory(String category) {
        this.category = category;
    }
    public void setCount(Integer count) {
        this.count = count;
    }
    public void setImgUrll(String imgUrll) {
        this.imgUrl = imgUrl;
    }
}
