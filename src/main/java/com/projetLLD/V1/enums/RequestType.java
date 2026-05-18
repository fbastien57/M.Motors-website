package com.projetLLD.V1.enums;

public enum RequestType {

    SALE("Achat"),
    RENTAL("Location");

    private final String label;

    RequestType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
