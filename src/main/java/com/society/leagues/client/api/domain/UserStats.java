package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserStats {

    Map<String,Object> all;
    List<Map<String,Object>> division;
    List<Map<String,Object>> season;
    List<Map<String,Object>> challenge;

    public Map<String,Object> getAll() {
        return all;
    }

    public List<Map<String, Object>> getDivision() {
        return division;
    }

    public List<Map<String, Object>> getSeason() {
        return season;
    }

    public List<Map<String, Object>> getChallenge() {
        return challenge;
    }

    public void setAll(Map<String, Object> all) {
        this.all = all;
    }

    public void setDivision(List<Map<String, Object>> division) {
        this.division = division;
    }

    public void setSeason(List<Map<String, Object>> season) {
        this.season = season;
    }

    public void setChallenge(List<Map<String, Object>> challenge) {
        this.challenge = challenge;
    }
}
