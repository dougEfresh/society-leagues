package com.society.leagues.client.api.domain;

import java.util.Date;

public class Challenge extends LeagueObject {
    Status status;
    TeamMatch teamMatch;
    Date challengeDate;

    public Challenge() {
    }

    public Challenge(TeamMatch teamMatch, Status status, Date date) {
        this.teamMatch = teamMatch;
        this.status = status;
        this.challengeDate = date;
    }

    public Date getChallengeDate() {
        return challengeDate;
    }

    public void setChallengeDate(Date challengeDate) {
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
