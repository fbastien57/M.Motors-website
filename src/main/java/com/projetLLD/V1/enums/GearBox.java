package com.projetLLD.V1.enums;

public enum GearBox {
    MANUAL("Manuelle"),
    AUTOMATIC("Automatique"),
    SEMI_AUTOMATIC("Semi-automatique");

    private final String label;

    GearBox(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
