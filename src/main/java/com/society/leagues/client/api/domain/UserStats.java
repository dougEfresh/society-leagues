package com.society.leagues.client.api.domain;

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

    public List<Map<String, Object>> getHandicapAll() {
        List<Map<String, Object>> handicapStats = new ArrayList<>();
        for (Map<String, Object> handicapStat : handicapAll) {
            Integer hc = (Integer) handicapStat.get("handicap");
            handicapStat.put("handicap",Handicap.values()[hc]);
        }
        return handicapAll;
    }

    public List<Map<String, Object>> getHandicapSeason() {
        List<Map<String, Object>> handicapStats = new ArrayList<>();
        for (Map<String, Object> handicapStat : handicapSeason) {
            Integer hc = (Integer) handicapStat.get("handicap");
            handicapStat.put("handicap",Handicap.values()[hc]);
        }
        return handicapSeason;
    }

    public List<Map<String, Object>> getHandicapDivision() {
        List<Map<String, Object>> handicapStats = new ArrayList<>();
        for (Map<String, Object> handicapStat : handicapDivision) {
            Integer hc = (Integer) handicapStat.get("handicap");
            handicapStat.put("handicap",Handicap.values()[hc]);
        }
        return handicapDivision;
    }

     public List<Map<String, Object>> getHandicapChallenge() {
        List<Map<String, Object>> handicapStats = new ArrayList<>();
        for (Map<String, Object> handicapStat : handicapChallenge) {
            Integer hc = (Integer) handicapStat.get("handicap");
            handicapStat.put("handicap",Handicap.values()[hc]);
        }
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
