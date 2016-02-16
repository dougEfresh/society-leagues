package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.converters.DateDeSerializer;
import com.society.leagues.converters.DateSerializer;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoField;
import java.util.Comparator;

public class Season extends LeagueObject   {

    @JsonView(PlayerResultSummary.class) String name;
    @NotNull
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    @JsonView(PlayerResultSummary.class) LocalDateTime startDate;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime endDate;
    @JsonView(PlayerResultSummary.class) @NotNull Integer rounds = -1;
    @JsonView(PlayerResultSummary.class) @NotNull Status seasonStatus;
    @JsonView(PlayerResultSummary.class) @NotNull Division division;
    String year = LocalDateTime.now().toString().substring(0,4);
    String type;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    @JsonSerialize(using = DateSerializer.class)
    @JsonDeserialize(using = DateDeSerializer.class)
    @JsonView(PlayerResultSummary.class)
    LocalDate sDate;

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
        s.setDivision(Division.UNKNOWN);
        s.setName("");
        s.setId("-1");
        s.setRounds(16);
        return s;
    }

    public LocalDate getsDate() {
        if (sDate == null && startDate != null)
            return startDate.toLocalDate();

        return sDate;
    }

    public void setsDate(LocalDate sDate) {
        this.sDate = sDate;
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
        if (name != null  && !name.isEmpty())
            return name;

        return String.format("%s,%s,%s",year,type,division.day);
    }

    public String getDisplayName() {
        if (this.name != null && !this.name.isEmpty()) {
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

        String name = "'" ;
         if (getStartDate() == null )  {
             name += year.substring(2,4) + " ";
         } else {
             name += getStartDate().toString().substring(2,4) + " ";
         }

        name += getSeasonType().getSeasonProperName();
        name += " " + getDayName();
        return name;
    }

    @JsonIgnore
    public SeasonType getSeasonType() {
        if (getStartDate() == null)
            return SeasonType.UNKNOWN;
        int month = getsDate().get(ChronoField.MONTH_OF_YEAR);
        if ( month < 6) {
            return SeasonType.WINTER;
        }
        if (month >= 6 && month <= 9) {
            return SeasonType.SUMMER;
        }
        return SeasonType.FALL;
    }

    public boolean isScramble() {
        return getDivision() == Division.MIXED_MONDAYS_MIXED;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getStartDate() {
        if (sDate != null)
            return sDate.atStartOfDay();

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

    public String getDay() {
       return division.day;
    }


    public String getDayName() {
       return division.day.substring(0,1).toUpperCase() + division.day.substring(1);
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
