package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChallengeRequest {
    Player challenger;
    Player opponent;
    List<LocalDateTime> date = new ArrayList<>();


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

    public List<LocalDateTime> getDate() {
        return date;
    }

    public void setDate(List<LocalDateTime> date) {
        this.date = date;
    }

    public void addDate(LocalDateTime date) {

    }
}
