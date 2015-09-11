package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.views.IdView;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;

public class LeagueObject implements Comparable<LeagueObject>{

    @Id String id;
    Integer legacyId;

    @JsonView(IdView.class)
    public String getId() {
        return id;
    }

    public Integer getLegacyId() {
        return legacyId;
    }

    public void setLegacyId(Integer legacyId) {
        this.legacyId = legacyId;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null)
            return false;

        if (this == o) return true;
        if (!(o instanceof LeagueObject)) return false;

        LeagueObject that = (LeagueObject) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    public void merge(LeagueObject object) {
        ReflectionUtils.shallowCopyFieldState(object,this);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public int compareTo(LeagueObject o) {
        return this.getId().compareTo(o.getId());
    }
}
