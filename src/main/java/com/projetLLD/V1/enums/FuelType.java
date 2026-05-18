package com.projetLLD.V1.enums;

public enum FuelType {
    ESSENCE("Essence"),
    DIESEL("Diesel"),
    ELECTRIC("Électrique"),
    HYBRID("Hybride");

    private final String label;

    FuelType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
