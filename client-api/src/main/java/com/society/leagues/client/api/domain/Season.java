package com.society.leagues.client.api.domain;

import com.owlike.genson.annotation.JsonIgnore;
import com.society.leagues.client.api.domain.division.Division;

import javax.validation.constraints.NotNull;
import java.sql.Date;


public class Season extends LeagueObject {

    @NotNull
    Division division;
    @NotNull
    String name;
    @NotNull
    LocalDate startDate;
    LocalDate endDate;

    public Season(Division division, String name, LocalDate startDate) {
        this.division = division;
        this.name = name;
        this.startDate = startDate;
    }

    public Season() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }
    
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    @JsonIgnore
    public Date getSqlStartDate() {
        return Date.valueOf(startDate.getLocalDate());
    }

    @JsonIgnore
    public void setSqlStartDate(Date startDate) {
        this.startDate = new LocalDate(startDate);
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    @JsonIgnore
    public void setSqlEndDate(Date endDate) {
        this.endDate = new LocalDate(endDate);
    }
}
