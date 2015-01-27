package com.society.leagues.client.api.domain;

public class Challenge extends LeagueObject {
    
    Player challenger;
    Player opponent;
    Slot slot;
    Status status;
    Match match;
    
    public Challenge() {
    }

    public Challenge(Player challenger, Player opponent, Slot slot) {
        this.challenger = challenger;
        this.opponent = opponent;
        this.slot = slot;
    }

    public Player getChallenger() {
        return challenger;
    }

    public void setChallenger(Player challenger) {
        this.challenger = challenger;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
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
