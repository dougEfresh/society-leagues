package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class Challenge extends LeagueObject {
    @NotNull Status status;
    @NotNull @DBRef Team challenger;
    @DBRef Team opponent;
    @NotNull @DBRef List<Slot> slots;
    @DBRef Slot acceptedSlot;
    @DBRef TeamMatch teamMatch;
    String message;

    public Challenge() {
    }
    public Challenge(String id) {
        this.id = id;
    }

    public Challenge(Status status, Team challenger, Team opponent, List<Slot> slots) {
        this.status = status;
        this.challenger = challenger;
        this.opponent = opponent;
        this.slots = slots;
    }

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public Slot getAcceptedSlot() {
        return acceptedSlot;
    }

    public void setAcceptedSlot(Slot acceptedSlot) {
        this.acceptedSlot = acceptedSlot;
    }

    public Team getChallenger() {
        return challenger;
    }

    public void setChallenger(Team challenger) {
        this.challenger = challenger;
    }

    public Team getOpponent() {
        return opponent;
    }

    public void setOpponent(Team opponent) {
        this.opponent = opponent;
    }

    public List<Slot> getSlots() {
        return slots;
    }

    public void setSlots(List<Slot> slots) {
        this.slots = slots;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonIgnore
    public Season getSeason() {
        return challenger.getSeason();
    }

    @JsonIgnore
    public boolean isCancelled() {
        return status == Status.CANCELLED;
    }

    @JsonIgnore
    public LocalDate getLocalDate() {
        if (slots == null || slots.isEmpty() || slots.get(0) == null)
            return LocalDate.MIN;

        return slots.get(0).getLocalDateTime().toLocalDate();
    }

    @JsonIgnore
    public String getDate() {
        if (slots == null || slots.isEmpty() || slots.iterator().next().getLocalDateTime() == null)
            return null;

        return slots.get(0).getLocalDateTime().toString();
    }

    @JsonIgnore
    public User getUserChallenger() {
        if (challenger == null || challenger.getMembers() == null || challenger.getMembers().getMembers().isEmpty())
            return null;
        return challenger.getMembers().getMembers().iterator().next();
    }

    public Handicap getUserChallengerHandicap() {
        if (getUserChallenger() == null || getSeason() == null) {
            return Handicap.NA;
        }

        return getUserChallenger().getHandicap(getSeason());
    }

    @JsonIgnore
    public User getUserOpponent() {
        if (opponent == null || opponent.getMembers() == null || opponent.getMembers().getMembers().isEmpty())
            return null;

        return opponent.getMembers().getMembers().iterator().next();
    }

    @JsonIgnore
    public String getRace() {
        return Handicap.race(getUserChallenger().getHandicap(getSeason()),getUserOpponent().getHandicap(getSeason()));
    }

    public Status getStatus(User user) {
        if (isBroadcast())
            return Status.BROADCAST;

        if (status == Status.ACCEPTED)
            return status;

        if (opponent.getChallengeUser().equals(user))
            return Status.PENDING;

        return Status.SENT;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @JsonIgnore
    public boolean isBroadcast() {
        if (opponent == null || opponent.getId().equals("-1")) {
            return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return "Challenge{" +
                "status=" + status +
                ", challenger=" + challenger +
                ", opponent=" + opponent +
                ", slots=" + slots +
                ", acceptedSlot=" + acceptedSlot +
                '}';
    }

    public boolean hasUser(User u) {
        if (u == null || challenger == null || opponent == null)
            return false;

        return challenger.hasUser(u) ||  opponent.hasUser(u);
    }


    public boolean hasTeam(Team t) {
        if (t == null)
            return false;

        return t.equals(challenger) || t.equals(opponent);
    }
}
