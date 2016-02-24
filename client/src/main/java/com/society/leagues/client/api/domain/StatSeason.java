package com.society.leagues.client.api.domain;

public class StatSeason {
    Integer year;
    SeasonType type;

    public StatSeason(Integer year, SeasonType type) {
        this.year = year;
        this.type = type;
    }

    public StatSeason() {
    }

    public String getName() {
        return "'" + (year-2000) + " " + type.getSeasonProperName();
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }


    public SeasonType getType() {
        return type;
    }

    public void setType(SeasonType type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StatSeason that = (StatSeason) o;

        if (!year.equals(that.year)) return false;
        return type.equals(that.type);

    }

    @Override
    public int hashCode() {
        int result = year.hashCode();
        result = 31 * result + type.hashCode();
        return result;
    }
}
