package com.society.leagues.client.api.domain.billing;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.User;

import java.time.LocalDateTime;

public class Billing extends LeagueObject {

    User user;
    String stripeId;
    BillingPackage billingPackage;
    Integer amount;
    Status status;
    LocalDateTime created;
    LocalDateTime updated;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getStripeId() {
        return stripeId;
    }

    public void setStripeId(String stripeId) {
        this.stripeId = stripeId;
    }

    public BillingPackage getBillingPackage() {
        return billingPackage;
    }

    public void setBillingPackage(BillingPackage billingPackage) {
        this.billingPackage = billingPackage;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public LocalDateTime getUpdated() {
        return updated;
    }

    public void setUpdated(LocalDateTime updated) {
        this.updated = updated;
    }
}
