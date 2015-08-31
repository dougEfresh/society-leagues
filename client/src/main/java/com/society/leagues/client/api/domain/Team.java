package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.api.domain.converters.DateTimeDeSerializer;
import com.society.leagues.client.api.domain.converters.DateTimeSerializer;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

public class Team extends LeagueObject {

    @DBRef Season season;
    @NotNull String name;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created;

    public Team(Season season, String name) {
        this.season = season;
        this.name = name;
        this.created = LocalDateTime.now();
    }

    public Team() {
    }

    public Season getSeason() {
        return season;
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
