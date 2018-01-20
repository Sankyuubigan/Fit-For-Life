package com.lenar.healthyfit.models;

/**
 * Created by Nilden on 20.01.2018.
 */

public class Food {
    String name;
    double kkal;

    public Food(String name, double kkal) {
        this.name = name;
        this.kkal = kkal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getKkal() {
        return kkal;
    }

    public void setKkal(double kkal) {
        this.kkal = kkal;
    }
}
