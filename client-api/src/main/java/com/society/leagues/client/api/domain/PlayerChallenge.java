package com.society.leagues.client.api.domain;

import java.time.LocalDate;
import java.util.List;

public class PlayerChallenge {
    Player challenger;
    Player opponent;
    LocalDate date;
    List<Challenge> challenges;

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

    public List<Challenge> getChallenges() {
        return challenges;
    }

    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PlayerChallenge)) return false;

        PlayerChallenge that = (PlayerChallenge) o;

        if (challenger != null ? !challenger.equals(that.challenger) : that.challenger != null) return false;
        if (opponent != null ? !opponent.equals(that.opponent) : that.opponent != null) return false;
        return !(date != null ? !date.equals(that.date) : that.date != null);

    }

    @Override
    public int hashCode() {
        int result = challenger != null ? challenger.hashCode() : 0;
        result = 31 * result + (opponent != null ? opponent.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
