package com.projetLLD.V1.enums;

public enum RequestStatus {

    PENDING("En attente"),
    APPROVED("Validée"),
    REJECTED("Refusée"),
    MISSING_DOCUMENT("Documents manquants");

    private final String label;

    RequestStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
