package com.society.leagues.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "client.api", ignoreUnknownFields = true)
public class ClientApiProperties {

    String endpoint;
    String team;
    String stat;
    String user;
    String season;
    String teamMatch;
    String playerResult;

    public String getPlayerResult() {
        return playerResult;
    }

    public void setPlayerResult(String playerResult) {
        this.playerResult = playerResult;
    }

    public String getSeason() {
        return season;
    }

    public void setSeason(String season) {
        this.season = season;
    }

    public String getStat() {
        return stat;
    }

    public void setStat(String stat) {
        this.stat = stat;
    }

    public String getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(String teamMatch) {
        this.teamMatch = teamMatch;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTeam() {
        return team == null ? "" : team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public boolean teamDebug() {
        return getTeam().equals("debug");
    }
}
