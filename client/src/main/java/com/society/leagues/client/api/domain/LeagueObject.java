package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.views.IdView;
import com.society.leagues.client.views.PlayerResultSummary;
import org.springframework.data.annotation.Id;
import org.springframework.util.ReflectionUtils;

public class LeagueObject implements Comparable<LeagueObject>{

    @JsonView(PlayerResultSummary.class) @Id String id;
    Integer legacyId;
    Boolean deleted = false;

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

    public boolean isDeleted() {
        return deleted == null || !deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public void merge(LeagueObject object) {
        ReflectionUtils.shallowCopyFieldState(object,this);
    }

    @SuppressWarnings("unchecked")
    public static <T extends LeagueObject> T copy(T src) {
        try {
            T dest = (T) src.getClass().newInstance();
            ReflectionUtils.shallowCopyFieldState(src,dest);
            return dest;
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
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
