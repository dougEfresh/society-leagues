package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.util.Date;

public class Challenge extends LeagueObject {
    Status status;
    LocalDateTime challengeDate;
    Player challenger;
    Player opponent;

    public Challenge() {
    }

    public LocalDateTime getChallengeDate() {
        return challengeDate;
    }

    public void setChallengeDate(LocalDateTime challengeDate) {
        this.challengeDate = challengeDate;
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
}
