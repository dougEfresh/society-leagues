package com.society.leagues.client.api.domain;

import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.util.List;

public class Challenge extends LeagueObject {
    @NotNull Status status;
    @NotNull @DBRef Team challenger;
    @NotNull @DBRef Team opponent;
    @NotNull @DBRef List<Slot> slots;
    @DBRef Slot acceptedSlot;

    public Challenge() {
    }

    public Challenge(Status status, Team challenger, Team opponent, List<Slot> slots) {
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

    public Team getChallenger() {
        return challenger;
    }

    public void setChallenger(Team challenger) {
        this.challenger = challenger;
    }

    public Team getOpponent() {
        return opponent;
    }

    public void setOpponent(Team opponent) {
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

    public Season getSeason() {
        return challenger.getSeason();
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "status=" + status +
                ", challenger=" + challenger +
                ", opponent=" + opponent +
                ", slots=" + slots +
                ", acceptedSlot=" + acceptedSlot +
                '}';
    }
}
