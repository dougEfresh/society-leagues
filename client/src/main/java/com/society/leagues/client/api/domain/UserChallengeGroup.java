package com.society.leagues.client.api.domain;

import com.society.leagues.adapters.ChallengeAdapter;
import com.society.leagues.client.api.domain.division.DivisionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class UserChallengeGroup {
    User challenger;
    User opponent;
    LocalDate date;
    List<Challenge> challenges = new ArrayList<>();
    Status status;

    public Integer getChallenger() {
        return challenger.getId();
    }

    public void setChallenger(User challenger) {
        this.challenger = challenger;
    }

    public Integer getOpponent() {
        return opponent.getId();
    }

    public void setOpponent(User opponent) {
        this.opponent = opponent;
    }

    public List<Challenge> challenges() {
        return challenges;
    }

    public List<ChallengeAdapter> getChallenges() {
        if (challenges == null) {
            return Collections.emptyList();
        }
        return challenges.stream().map(ChallengeAdapter::new).collect(Collectors.toList());
    }
    public void setChallenges(List<Challenge> challenges) {
        this.challenges = challenges;
    }

    public void addChallenge(Challenge challenge) {
        challenges.add(challenge);
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public Set<DivisionType> getGames() {
        Set<DivisionType> games = new HashSet<>();
        for (Challenge challenge : challenges()) {
            games.add(challenge.getChallenger().getDivision().getType());
        }
        return games;
    }

    public Set<Slot> getSlots() {
        Set<Slot> slots = new HashSet<>();
        for (Challenge challenge : challenges()) {
            slots.add(challenge.getSlot());
        }
        return slots;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status status() {
            return status;
    }

    public DivisionType getSelectedGame() {
        if (challenges.size() == 1){
            return challenges.get(0).getChallenger().getDivision().getType();
        }
        return null;
    }

    public Integer getSelectedSlot() {
        if (challenges.size() == 1){
            return challenges.get(0).getSlot().getId();
        }
        return 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserChallengeGroup)) return false;

        UserChallengeGroup group = (UserChallengeGroup) o;

        if (challenger != null ? !challenger.equals(group.challenger) : group.challenger != null) return false;
        if (opponent != null ? !opponent.equals(group.opponent) : group.opponent != null) return false;
        return !(date != null ? !date.equals(group.date) : group.date != null);

    }

    @Override
    public String toString() {
        return "UserChallengeGroup{" +
                "challenger=" + challenger +
                ", opponent=" + opponent +
                ", date=" + date +
                ", challenges=" + challenges +
                ", status=" + status +
                '}';
    }

    @Override
    public int hashCode() {
        int result = challenger != null ? challenger.hashCode() : 0;
        result = 31 * result + (opponent != null ? opponent.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        return result;
    }
}
