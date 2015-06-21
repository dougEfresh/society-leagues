package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Status;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class SeasonAdapter {

    Season season;

    public SeasonAdapter(Season season) {
        this.season = season;
    }

    public SeasonAdapter() {
    }

    public Integer getId() {
        return season.getId();
    }

    public String getName() {
        return season.getName();
    }

    public Date getStartDate() {
        return season.getStartDate();
    }

    public Date getEndDate() {

        return season.getEndDate();
    }

    public Status getStatus() {
        return season.getSeasonStatus();
    }

    public Integer getDivision() {
        return season.getDivision().getId();
    }
}
