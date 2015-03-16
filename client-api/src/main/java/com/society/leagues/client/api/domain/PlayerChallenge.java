package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class PlayerChallenge {
    Player challenger;
    Player opponent;
    List<LocalDateTime> challengeDates;
    Status status;

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

    public List<LocalDateTime> getChallengeDates() {
        return challengeDates;
    }

    public void setChallengeDates(List<LocalDateTime> challengeDates) {
        this.challengeDates = challengeDates;
    }


}
