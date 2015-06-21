package com.society.leagues.client.api.domain.division;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.society.leagues.client.api.domain.LeagueObject;

import javax.validation.constraints.NotNull;

public class Division extends LeagueObject {
    @NotNull DivisionType type;

    public Division(DivisionType type) {
        this.type = type;
        }

    public Division() {
    }

    public DivisionType getType() {
        return type;
    }

    public void setType(DivisionType type) {
        this.type = type;
    }

    public LeagueType league() {
        return type.getLeagueType();
    }
    
    @Override
    public String toString() {
        return "Division{" +
                "type=" + type +
                ", id=" + id +
                '}';
    }

    public boolean isChallenge() {
        return this.getType() == DivisionType.NINE_BALL_CHALLENGE || this.getType() == DivisionType.EIGHT_BALL_CHALLENGE;
    }

    @JsonIgnore
    @Override
    public Integer getId() {
        return super.getId();
    }
}
