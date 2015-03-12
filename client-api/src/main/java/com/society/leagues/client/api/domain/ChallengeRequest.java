package com.society.leagues.client.api.domain;

import java.sql.Time;
import java.util.Date;

public class ChallengeRequest {
    Player challenger;
    Player opponent;
    Date date;


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

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
