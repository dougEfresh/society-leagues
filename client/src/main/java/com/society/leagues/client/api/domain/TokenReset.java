package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;

public class TokenReset {
    String token;
    LocalDateTime created = LocalDateTime.now();

    public TokenReset(String token) {
        this.token = token;
    }

    public TokenReset() {
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TokenReset reset = (TokenReset) o;

        return token.equals(reset.token);

    }

    @Override
    public int hashCode() {
        return token.hashCode();
    }

    @Override
    public String toString() {
        return "TokenReset{" +
                "token='" + token + '\'' +
                ", created=" + created +
                '}';
    }
}
