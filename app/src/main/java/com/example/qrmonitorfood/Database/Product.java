package com.example.qrmonitorfood.Database;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class Product {

    private String produktId;
    private String title;
    private String dateOfMade;
    private String dateExpiration;
    private String batch;
    private String producerId;
    private String decription;
    private List<String> products;


    public Product(String title, String dateOfMade, String dateExpiration, String batch, String producerId, String decription, List<String> products) throws ParseException {
        this.title = title;
        this.dateOfMade = dateOfMade;
        this.dateExpiration = dateExpiration;
        this.batch = batch;
        this.producerId = producerId;
        this.decription = decription;
        this.products = products;
    }

    public Product() {
        products = new ArrayList<>();
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

    public String getBatch() {
        return batch;
    }

    public void setBatch(String batch) {
        this.batch = batch;
    }

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }

    public String getDecription() {
        return decription;
    }

    public void setDecription(String decription) {
        this.decription = decription;
    }

    public List<String> getProducts() {
        return products;
    }

    public void setProducts(List<String> products) {
        this.products = products;
    }


}

