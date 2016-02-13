package com.society.leagues.client.api.domain;

public enum SeasonType {
    UNKNOWN("unknown",0),
    WINTER("winter",1),
    SUMMER("summer",2),
    SPRING("spring",3),
    FALL("fall",4);

    final String season;
    final Integer order;

    SeasonType(String season, Integer order) {
        this.season = season;
        this.order = order;
    }

    public String getSeason() {
        return season;
    }

    public Integer getOrder() {
        return order;
    }

    public String getSeasonQuater() {
        switch (this) {
            case WINTER:
                return "Q1";
            case SUMMER:
                return "Q2";
            case FALL:
                return "Q3";
        }
        return "Q4";
    }


    public String getSeasonProperName() {
        return season.substring(0,1).toUpperCase() + season.substring(1);
    }

    public static SeasonType fromName(String name) {
        return SeasonType.valueOf(name.toUpperCase());
    }
}
