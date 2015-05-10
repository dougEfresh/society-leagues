package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class UserStats {

    Map<String,Object> all;
    List<Map<String,Object>> division;
    List<Map<String,Object>> season;
    List<Map<String,Object>> challenge;
    List<Map<String,Object>> handicapAll;
    List<Map<String,Object>> handicapDivision;
    List<Map<String,Object>> handicapSeason;
    List<Map<String,Object>> handicapChallenge;

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

    @JsonIgnore
    public List<Map<String, Object>> getHandicapAll() {
        return handicapAll;
    }

    @JsonIgnore
    public List<Map<String, Object>> getHandicapSeason() {
        return handicapSeason;
    }

    @JsonIgnore
    public List<Map<String, Object>> getHandicapDivision() {
        return handicapDivision;
    }

    @JsonIgnore
     public List<Map<String, Object>> getHandicapChallenge() {
        return handicapChallenge;
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

    public void setHandicapAll(List<Map<String, Object>> handicap) {
        this.handicapAll= handicap;
    }

    public void setHandicapDivision(List<Map<String, Object>> handicap) {
        this.handicapDivision= handicap;
    }

    public void setHandicapSeason(List<Map<String, Object>> handicap) {
        this.handicapSeason= handicap;
    }

    public void setHandicapChallenge(List<Map<String, Object>> handicap) {
        this.handicapChallenge= handicap;
    }
}
