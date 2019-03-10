package com.example.qrmonitorfood.Database;

import java.util.List;

public class Zlozky {

    String nazov;
    String id_zlozky;
    List<Zlozky> zlozky;
    String id_produkt;

    public Zlozky(String nazov, String id_zlozky, List<Zlozky> zlozky, String id_produkt) {
        this.nazov = nazov;
        this.id_zlozky = id_zlozky;
        this.zlozky = zlozky;
        this.id_produkt = id_produkt;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getId_zlozky() {
        return id_zlozky;
    }

    public void setId_zlozky(String id_zlozky) {
        this.id_zlozky = id_zlozky;
    }

    public List<Zlozky> getZlozky() {
        return zlozky;
    }

    public void setZlozky(List<Zlozky> zlozky) {
        this.zlozky = zlozky;
    }

    public String getId_produkt() {
        return id_produkt;
    }

    public void setId_produkt(String id_produkt) {
        this.id_produkt = id_produkt;
    }
}
