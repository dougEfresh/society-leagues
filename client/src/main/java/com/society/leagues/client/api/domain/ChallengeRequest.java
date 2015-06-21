package com.society.leagues.client.api.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChallengeRequest {
    User challenger;
    User opponent;
    boolean nine;
    boolean eight;
    List<Slot> slots = new ArrayList<>();

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

    public boolean isNine() {
        return nine;
    }

    public void setNine(boolean nine) {
        this.nine = nine;
    }

    public boolean isEight() {
        return eight;
    }

    public void setEight(boolean eight) {
        this.eight = eight;
    }

    public User getChallenger() {
        return challenger;
    }

    public void setChallenger(User challenger) {
        this.challenger = challenger;
    }

    @Override
    public String toString() {
        return "ChallengeRequest{" +
                ", opponent=" + opponent +
                ", challengeTimes=" + Arrays.toString(slots.toArray()) +
                '}';
    }
}
