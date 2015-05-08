package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Challenge;

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
}
