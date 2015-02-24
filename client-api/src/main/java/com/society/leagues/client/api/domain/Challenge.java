package com.society.leagues.client.api.domain;

public class Challenge extends LeagueObject {
    
    Slot slot;
    Status status;
    Match match;

    public Challenge() {
    }

    public Challenge(Match match, Slot slot) {
        this.match = match;
        this.slot = slot;
    }

    public Slot getSlot() {
        return slot;
    }

    public void setSlot(Slot slot) {
        this.slot = slot;
    }

    public Match getMatch() {
        return match;
    }

    public void setMatch(Match match) {
        this.match = match;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
