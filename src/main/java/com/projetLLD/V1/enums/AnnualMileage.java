package com.projetLLD.V1.enums;

public enum AnnualMileage {

    KM10000(10000),
    KM15000(15000),
    KM20000(20000);


    private final int km;

    AnnualMileage(int km) {
        this.km = km;
    }

    public int getKm() {
        return km;
    }

    public String getLabel() {
        return String.format("%,d km", km)
                .replace(",", " ");
    }
}
