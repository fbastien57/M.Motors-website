package com.projetLLD.V1.enums;


public enum RentalDuration {

    M12(12),
    M24(24),
    M36(36),
    M48(48);

    private final int months;

    RentalDuration(int months) {
        this.months = months;
    }

    public int getMonths() {
        return months;
    }

}
