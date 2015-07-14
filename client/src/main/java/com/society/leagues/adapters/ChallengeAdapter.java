package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.division.DivisionType;

import java.time.LocalDateTime;

public class ChallengeAdapter {

    Challenge challenge;

    public ChallengeAdapter(Challenge challenge) {
        this.challenge = challenge;
    }

    public ChallengeAdapter() {

     }

    public Integer getId() {
        return challenge.getId();
    }

    public Slot getSlot() {
        return challenge.getSlot();
    }

    public Integer getChallenger() {
        return challenge.getChallenger().getUserId();
    }

    public DivisionType getGame() {
        return challenge.getChallenger().getDivision().getType();
    }

    public Integer getOpponent() {
        return challenge.getOpponent().getUserId();
    }

    public Status getStatus() {
        return challenge.getStatus();
    }

    public Integer getTeamMatchId() {
        return challenge.getTeamMatch() == null ? 0 : challenge.getTeamMatch().getId();
    }
}
