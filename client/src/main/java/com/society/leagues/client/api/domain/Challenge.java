package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

public class Challenge extends LeagueObject {
    Status status;
    @DBRef User challenger;
    @DBRef User opponent;
    @DBRef List<Slot> slots;
    @DBRef Slot acceptedSlot;
    TeamMatch teamMatch;

    public Challenge() {
    }

    public Challenge(Status status, User challenger, User opponent, List<Slot> slots) {
        this.status = status;
        this.challenger = challenger;
        this.opponent = opponent;
        this.slots = slots;
    }

    public Slot getAcceptedSlot() {
        return acceptedSlot;
    }

    public void setAcceptedSlot(Slot acceptedSlot) {
        this.acceptedSlot = acceptedSlot;
    }

    public User getChallenger() {
        return challenger;
    }

    public void setChallenger(User challenger) {
        this.challenger = challenger;
    }

    public User getOpponent() {
        return opponent;
    }

    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

}
