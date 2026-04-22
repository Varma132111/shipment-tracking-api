package com.logistics.shipmenttracking.security;

public class CurrentUser {
    private final String companyId;

    public CurrentUser(String companyId) {
        this.companyId = companyId;
    }

    public String getCompanyId() {
        return companyId;
    }
}
