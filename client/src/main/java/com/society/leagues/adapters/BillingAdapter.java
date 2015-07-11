package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.billing.Billing;
import com.society.leagues.client.api.domain.billing.BillingPackage;

import java.time.LocalDateTime;

public class BillingAdapter {

    Billing billing;

    public BillingAdapter() {
    }

    public BillingAdapter(Billing billing) {
        this.billing = billing;
    }

    public Integer getUserId() {
        return billing.getUser().getId();
    }

    public Integer getId() {
        return billing.getId();
    }

    public Integer getAmount() {
        return billing.getAmount();
    }

    public BillingPackage getPackage() {
        return billing.getBillingPackage();
    }

    public LocalDateTime getCreated() {
        return billing.getCreated();
    }

    public LocalDateTime getUpdated() {
        return billing.getUpdated();
    }

    public Status getStatus() {
        return billing.getStatus();
    }
}
