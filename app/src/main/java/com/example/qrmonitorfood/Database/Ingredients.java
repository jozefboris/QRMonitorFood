package com.example.qrmonitorfood.Database;


import java.util.List;

public class Ingredients  {

    String title;
    String id_ingredient;
    List<Zlozky> ingredients;
    String id_product;

    public Ingredients(String title, String id_ingredient, List<Zlozky> ingredients, String id_product) {
        this.title = title;
        this.id_ingredient = id_ingredient;
        this.ingredients = ingredients;
        this.id_product = id_product;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId_ingredient() {
        return id_ingredient;
    }

    public void setId_ingredient(String id_ingredient) {
        this.id_ingredient = id_ingredient;
    }

    public List<Zlozky> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<Zlozky> ingredients) {
        this.ingredients = ingredients;
    }

    public String getId_product() {
        return id_product;
    }

    public void setId_product(String id_product) {
        this.id_product = id_product;
    }
}
