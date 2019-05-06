package com.example.qrmonitorfood.Database;

public class Producer {

   private String title;
    private String producerId;


    public Producer( String title) {
        this.title = title;
    }

    public Producer() {}

    public Producer(String title, String id) {
        this.title = title;
        this.producerId = id;
    }

    public String getId() {
        return producerId;
    }

    public void setId(String id) {
        this.producerId = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return  title;
    }
}
