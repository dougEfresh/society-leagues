package com.society.admin.model;

import com.society.leagues.client.api.domain.TeamMatch;

import java.util.ArrayList;
import java.util.List;

public class TeamMatchModel {

    List<TeamMatch> matches = new ArrayList<>();

    public TeamMatchModel(List<TeamMatch> matches) {
        this.matches = matches;
    }

    public TeamMatchModel() {
    }

    public List<TeamMatch> getMatches() {
        return matches;
    }

    public void setMatches(List<TeamMatch> matches) {
        this.matches = matches;
    }
}
