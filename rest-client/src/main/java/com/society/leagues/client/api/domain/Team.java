package com.society.leagues.client.api.domain;

import java.util.Date;

public class Team extends LeagueObject {
    String name;
    Date created;

    public Team(String name) {
        this.name = name;

    }

    public Team() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
