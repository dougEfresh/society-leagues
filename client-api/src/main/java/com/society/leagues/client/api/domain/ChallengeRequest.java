package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChallengeRequest {
    Player challenger;
    Player opponent;
    List<Slot> slotTimes = new ArrayList<>();

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

    public List<Slot> getSlotTimes() {
        return slotTimes;
    }

    public void setSlotTimes(List<Slot> slotTimes) {
        this.slotTimes = slotTimes;
    }

    public void addDate(LocalDateTime date) {

    }

    @Override
    public String toString() {
        return "ChallengeRequest{" +
                "challenger=" + challenger +
                ", opponent=" + opponent +
                ", challengeTimes=" + Arrays.toString(slotTimes.toArray()) +
                '}';
    }
}
