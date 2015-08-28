package com.society.leagues.client.api.domain;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Season extends LeagueObject {

    @NotNull String name;
    @NotNull Date startDate;
    Date endDate;
    @NotNull Integer rounds;
    @NotNull Status seasonStatus;
    @NotNull Division division;

    public Season(String name, Date startDate, Integer rounds, Division division) {
        this.name = name;
        this.startDate = startDate;
        this.rounds = rounds;
        this.seasonStatus = Status.PENDING;
        this.division = division;
    }
    
    public Season(String name, Date startDate, Integer rounds, Status status) {
        this.name = name;
        this.startDate = startDate;
        this.rounds = rounds;
        this.seasonStatus = status;
    }

    public Season() {
    }

    public Status getSeasonStatus() {
        return seasonStatus;
    }

    public void setSeasonStatus(Status seasonStatus) {
        this.seasonStatus = seasonStatus;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    @Override
    public String toString() {
        return "Season{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
