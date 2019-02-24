package com.example.qrmonitorfood.Database;

import java.util.List;

public class Product {
    String nazov;
    String datum_vyroby;
    String datum_spotreby;
    Long sarse;
    String vyrobca;
    String popis;
    List<Product> produkty;
    List<Zlozky> zlozky;

    public Product(String nazov, String datum_vyroby, String datum_spotreby, Long sarse, String vyrobca, String popis, List<Product> produkty, List<Zlozky> zlozky) {
        this.nazov = nazov;
        this.datum_vyroby = datum_vyroby;
        this.datum_spotreby = datum_spotreby;
        this.sarse = sarse;
        this.vyrobca = vyrobca;
        this.popis = popis;
        this.produkty = produkty;
        this.zlozky = zlozky;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

    public String getDatum_vyroby() {
        return datum_vyroby;
    }

    public void setDatum_vyroby(String datum_vyroby) {
        this.datum_vyroby = datum_vyroby;
    }

    public String getDatum_spotreby() {
        return datum_spotreby;
    }

    public void setDatum_spotreby(String datum_spotreby) {
        this.datum_spotreby = datum_spotreby;
    }

    public Long getSarse() {
        return sarse;
    }

    public void setSarse(Long sarse) {
        this.sarse = sarse;
    }

    public String getVyrobca() {
        return vyrobca;
    }

    public void setVyrobca(String vyrobca) {
        this.vyrobca = vyrobca;
    }

    public String getPopis() {
        return popis;
    }

    public void setPopis(String popis) {
        this.popis = popis;
    }

    public List<Product> getProdukty() {
        return produkty;
    }

    public void setProdukty(List<Product> produkty) {
        this.produkty = produkty;
    }

    public List<Zlozky> getZlozky() {
        return zlozky;
    }

    public void setZlozky(List<Zlozky> zlozky) {
        this.zlozky = zlozky;
    }
}

