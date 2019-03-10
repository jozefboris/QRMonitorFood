package com.example.qrmonitorfood.Database;

import java.util.List;

public class Product {
    String produktId;
    String title;
    String dateOfMade;
    String dateExpiration;
    String count;
    String producer;
    String decription;
    List<Product> products;

    public Product(String produktId, String title, String dateOfMade, String dateExpiration, String count, String producer, String decription, List<Product> products) {
        this.produktId = produktId;
        this.title = title;
        this.dateOfMade = dateOfMade;
        this.dateExpiration = dateExpiration;
        this.count = count;
        this.producer = producer;
        this.decription = decription;
        this.products = products;
    }

    public String getProduktId() {
        return produktId;
    }

    public void setProduktId(String produktId) {
        this.produktId = produktId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateOfMade() {
        return dateOfMade;
    }

    public void setDateOfMade(String dateOfMade) {
        this.dateOfMade = dateOfMade;
    }

    public String getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(String dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getProducer() {
        return producer;
    }

    public void setProducer(String producer) {
        this.producer = producer;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}

