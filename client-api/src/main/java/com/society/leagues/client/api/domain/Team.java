package com.society.leagues.client.api.domain;

import javax.validation.constraints.NotNull;
import java.util.Date;

public class Team extends LeagueObject {

    @NotNull
    String name;
    Date created;
    TeamStatus status;
    
    public static final Team bye = new Team("Bye",null);

    public Team(String name) {
        this.name = name;
    }

    public Team(String name, Date created) {
        this.name = name;
        this.created = created;
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

    public TeamStatus getStatus() {
        return status;
    }

    public void setStatus(TeamStatus status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Team)) return false;

        Team team = (Team) o;

        if (!name.equals(team.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public String toString() {
        return "Team{" +
                "name='" + name + '\'' +
                ", id=" + id +
                '}';
    }
}
