package com.society.leagues.client.api.domain;

public class Challenge extends LeagueObject {
    Status status;
    Player challenger;
    Player opponent;
    Slot slot;
    TeamMatch teamMatch;

    public Challenge() {
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
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

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "status=" + status +
                ", challenger=" + challenger +
                ", opponent=" + opponent +
                ", slot=" + slot +
                '}';
    }
}
