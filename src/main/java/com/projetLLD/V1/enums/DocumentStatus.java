package com.projetLLD.V1.enums;

public enum DocumentStatus {
    UPLOADED("Envoyé"),
    VALIDATED("Validé"),
    REJECTED("Refusé"),
    MISSING("Manquant");

    private final String label;

    DocumentStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

