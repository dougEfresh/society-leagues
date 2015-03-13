package com.society.leagues.client.api.domain;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Date;

public class ChallengeRequest {
    Player challenger;
    Player opponent;
    LocalDateTime date;


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

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}
