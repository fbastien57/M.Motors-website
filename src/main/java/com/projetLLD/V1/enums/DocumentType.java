package com.projetLLD.V1.enums;

public enum DocumentType {
    ID_CARD("Carte d'identité"),
    DRIVER_LICENSE("Permis de conduire"),
    PROOF_OF_INCOME("Justificatif de revenus"),
    PROOF_OF_ADDRESS("Justificatif de domicile"),
    LOA_FORM("Dossier LOA"),
    SALE_FORM("Dossier vente");

    private final String label;

    DocumentType(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
