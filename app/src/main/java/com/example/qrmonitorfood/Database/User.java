package com.example.qrmonitorfood.Database;

public class User {

    String producerId;



    public User(String producerId) {
        this.producerId = producerId;
    }

    public  User(){}

    public String getProducerId() {
        return producerId;
    }

    public void setProducerId(String producerId) {
        this.producerId = producerId;
    }
}
