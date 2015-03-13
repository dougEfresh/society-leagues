package com.society.leagues.client.api.domain;

import java.time.LocalDateTime;
import java.util.Date;

public class Challenge extends LeagueObject {
    Status status;
    TeamMatch teamMatch;
    LocalDateTime challengeDate;

    public Challenge() {
    }

    public LocalDateTime getChallengeDate() {
        return challengeDate;
    }

    public void setChallengeDate(LocalDateTime challengeDate) {
        this.challengeDate = challengeDate;
    }

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

}
