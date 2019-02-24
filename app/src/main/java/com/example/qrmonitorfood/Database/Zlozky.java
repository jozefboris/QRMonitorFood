package com.example.qrmonitorfood.Database;

import java.util.List;

public class Zlozky {
    String nazov;
  //  List<Zlozky> zlozky;

    public Zlozky(String nazov) {
        this.nazov = nazov;
       // this.zlozky = zlozky;
    }

    public String getNazov() {
        return nazov;
    }

    public void setNazov(String nazov) {
        this.nazov = nazov;
    }

 /*   public List<Zlozky> getZlozky() {
        return zlozky;
    }*/

 /*   public void setZlozky(List<Zlozky> zlozky) {
        this.zlozky = zlozky;
    }*/
}
