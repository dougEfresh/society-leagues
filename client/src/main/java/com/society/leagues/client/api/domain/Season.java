package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Comparator;

public class Season extends LeagueObject   {

    String name;
    @NotNull
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime startDate;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime endDate;
    @NotNull Integer rounds = -1;
    @NotNull Status seasonStatus;
    @NotNull Division division;
    String year = LocalDateTime.now().toString().substring(0,4);
    String type;

    public Season(String name, LocalDateTime startDate, Integer rounds, Division division) {
        this.name = name;
        this.startDate = startDate;
        this.rounds = rounds;
        this.seasonStatus = Status.ACTIVE;
        this.division = division;
    }
    
    public Season(String name, LocalDateTime startDate, Integer rounds, Status status) {
        this.name = name;
        this.startDate = startDate;
        this.rounds = rounds;
        this.seasonStatus = status;
    }

    public Season() {
    }

    public static Season getDefault() {
        Season s = new Season();
        s.setSeasonStatus(Status.PENDING);
        s.setDivision(Division.NINE_BALL_TUESDAYS);
        s.setName("default");
        return s;
    }

    public Season(String id) {
        this.id = id;
    }

    public Status getSeasonStatus() {
        return seasonStatus;
    }

    public void setSeasonStatus(Status seasonStatus) {
        this.seasonStatus = seasonStatus;
    }

    public String getName() {
        if (name != null)
            return name;

        return String.format("%s,%s,%s",year,type,division);
    }

    public String getDisplayName() {
        if (this.name != null) {
            return this.name;
        }
        if (division == Division.NINE_BALL_CHALLENGE) {
            return "Top Gun";
        }
        if (year == null || division == null)
            return "";

        String name = "'" + year.substring(2,4) + " ";
        name += type;
        name += " " + division.displayName;
        return name;
    }

     public String getFormattedName() {
        if (division == Division.NINE_BALL_CHALLENGE) {
            return "Top Gun";
        }
        if (year == null || division == null)
            return "";

        String name = "'" + year.substring(2,4) + " ";
        name += type;
        name += " " + division.displayName;
        return name;
    }

    public boolean isScramble() {
        return getDivision() == Division.MIXED_MONDAYS_MIXED;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public Integer getRounds() {
        return rounds;
    }

    public void setRounds(Integer rounds) {
        this.rounds = rounds;
    }

    public Division getDivision() {
        return division;
    }

    public void setDivision(Division division) {
        this.division = division;
    }

    public boolean isActive() {
        return this.seasonStatus == Status.ACTIVE;
    }

    public boolean isNine(){
        return getDivision() ==  Division.NINE_BALL_CHALLENGE || getDivision() == Division.NINE_BALL_TUESDAYS;
    }

    public boolean isChallenge(){
        return getDivision().isChallenge();
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getShortName(){
        if (this.isChallenge())
            return "Top Gun";
        if (this.isNine())
            return "Tues 9 Ball";
        if (this.isScramble())
            return "Scramble";
        if (this.getDisplayName().toLowerCase().contains("wed"))
            return "Weds 8 Ball";

        return "Thurs 8 Ball";

    }

      public static Comparator<Season> sortOrder = new Comparator<Season>() {
        @Override
        public int compare(Season o1, Season o2) {
            return o1.getDivision().order.compareTo(o2.getDivision().order);
        }
    };

    public static Comparator<Season> sort = new Comparator<Season>() {
        @Override
        public int compare(Season o1, Season o2) {
            if (o1.isChallenge())
                return 1;
            if (o2.isChallenge())
                return 1;
            if (o1.getLegacyId() != null && o2.getLegacyId() != null)
                return o1.getLegacyId().compareTo(o2.getLegacyId());

            if (o1.getStartDate() != null && o2.getStartDate() != null)
                return o1.getStartDate().compareTo(o2.getStartDate());

            return 0;
        }
    };

    public boolean isTuesdayNine() {
        if (division == null)
            return false;

        return division == Division.NINE_BALL_TUESDAYS;
    }

    public boolean isEight(){
        if (division == null)
            return false;

        return division == Division.EIGHT_BALL_THURSDAYS || division == Division.EIGHT_BALL_WEDNESDAYS;
    }

    @Override
    public String toString() {
        return "Season{" +
                "name='" + name + '\'' +
                ", startDate=" + startDate +
                ", seasonStatus=" + seasonStatus +
                ", division=" + division +
                ", year='" + year + '\'' +
                ", type='" + type + '\'' +
                '}';
    }
}
