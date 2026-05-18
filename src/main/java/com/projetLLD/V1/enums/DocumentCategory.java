package com.projetLLD.V1.enums;

public enum DocumentCategory {

    LOA_FORM("Formulaire de location"),
    SALE_FORM("Formulaire d'achat"),
    TERMS_AND_CONDITIONS("Conditions générales"),
    OTHER("Autre");

    private final String label;

    DocumentCategory(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
