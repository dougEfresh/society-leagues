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
    List<Map<String,Object>> handicap;

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

    public List<Map<String, Object>> getHandicap() {
        List<Map<String, Object>> handicapStats = new ArrayList<>();
        for (Map<String, Object> handicapStat : handicap) {
            Integer hc = (Integer) handicapStat.get("handicap");
            handicapStat.put("handicap",Handicap.values()[hc]);
        }
        return handicap;
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

    public void setHandicap(List<Map<String, Object>> handicap) {
        this.handicap = handicap;
    }

}
