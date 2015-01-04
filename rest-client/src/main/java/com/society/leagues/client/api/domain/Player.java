package com.society.leagues.client.api.domain;

public class Player extends User {

    public boolean verify() {
        return email != null &&
                login != null &&
                firstName != null &&
                lastName != null &&
                password != null;
    }
}
