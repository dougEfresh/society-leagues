package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.views.PlayerResultSummary;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Stat {

    @JsonView(PlayerResultSummary.class)
    Integer wins = 0;
    @JsonView(PlayerResultSummary.class) Integer loses = 0;
    @JsonView(PlayerResultSummary.class) Integer racksWon = 0;
    @JsonView(PlayerResultSummary.class) Integer racksLost = 0;
    @JsonView(PlayerResultSummary.class) Integer setWins = 0;
    @JsonView(PlayerResultSummary.class) Integer setLoses = 0;
    @JsonView(PlayerResultSummary.class) Integer matches = 0;
    @JsonView(PlayerResultSummary.class) Integer forfeits = 0;
    @JsonView(PlayerResultSummary.class) StatType type;
    @JsonView(PlayerResultSummary.class) User user;
    @JsonView(PlayerResultSummary.class) Season season;
    @JsonView(PlayerResultSummary.class) Handicap handicap;
    @JsonView(PlayerResultSummary.class) Double points = 0d;
    @JsonView(PlayerResultSummary.class) Integer rank = 0;
    @JsonView(PlayerResultSummary.class) Team team;

    public Stat() {
    }

    public void setType(StatType type) {
        this.type = type;
    }

    @JsonView(PlayerResultSummary.class)
    public String getGame() {
        return getType() == StatType.MIXED_EIGHT ? "8" : "9";
    }

    public static Stat buildTeamStats(final Team team, final List<TeamMatch> matches) {
        Stat s = new Stat();
        s.setSeason(team.getSeason());
        List<TeamMatch> results = matches.stream().filter(TeamMatch::isHasResults).collect(Collectors.toList());
        s.matches = results.size();
        for (TeamMatch result : results) {
            if (result.isWinner(team)) {
                s.wins++;
                s.racksWon += result.getRacks(team);
                s.racksLost += result.getOpponentRacks(team);
                s.setWins += result.getSetWins(team);
                s.setLoses += result.getSetLoses(team);
            } else {
                s.loses++;
                s.setWins += result.getSetWins(team);
                s.setLoses += result.getSetLoses(team);
                s.racksLost += result.getOpponentRacks(team);
                s.racksWon += result.getRacks(team);
            }
            if (result.getHome().equals(team)) {
                s.forfeits += result.getHomeForfeits();
            } else {
                s.forfeits += result.getAwayForfeits();
            }
        }
        return s;
    }

    public static void calculate(User u, Stat s, List<PlayerResult> matches) {
        for (PlayerResult match : matches) {
            if (!match.hasUser(u) || !match.hasResults()) {
                continue;
            }
            s.matches++;
            if (match.isWinner(u)) {
                s.wins++;
                s.racksWon += match.getWinnerRacks();
                s.racksLost += match.getLoserRacks();
            } else {
                s.loses++;
                s.racksWon += match.getLoserRacks();
                s.racksLost += match.getWinnerRacks();
            }
        }
    }

    public static Stat buildPlayerSeasonStats(final User u, final Season season , final List<PlayerResult> matches) {
        Stat s = new Stat();
        s.setUser(u);
        s.setSeason(season);
        calculate(u,s,matches);
        return s;
    }


    public void setHandicap(Handicap handicap) {
        this.handicap = handicap;
    }

    public Integer getForfeits() {
        return forfeits;
    }

    public void setForfeits(Integer forfeits) {
        this.forfeits = forfeits;
    }

    @JsonView(PlayerResultSummary.class)
    public Handicap getHandicap() {
        if (handicap == null) {
            return Handicap.NA;
        }
        return handicap;
    }

    @JsonView(PlayerResultSummary.class)
    public boolean isLifeTime() {
        return getType().isLifetime();
    }

    @JsonView(PlayerResultSummary.class)
    public String getHandicapDisplay() {
        if (getType().isLifetime())
            return "";
        return getHandicap().getDisplayName();
    }

    public static Stat buildLifeTimeStats(final User u, final List<Stat> stats) {
        Stat s = new Stat();
        s.setUser(u);
        s.type = StatType.ALL;
        for (Stat stat : stats.stream().filter(st->st.getType() == StatType.USER_SEASON).collect(Collectors.toList())) {
            s.racksLost += stat.racksLost;
            s.racksWon += stat.racksWon;
            s.wins += stat.wins;
            s.loses += stat.loses;
            s.matches += stat.matches;
            s.setWins += stat.setWins;
            s.setLoses += stat.setLoses;
        }
        return s;
    }

    @JsonView(PlayerResultSummary.class)
    public StatType getType() {
        if (type != null) {
            return type;
        }

        if (user != null && season != null) {
            return StatType.USER_SEASON;
        }

        return StatType.UNKNOWN;
    }

    public Season getSeason() {
        if (season != null)
            return season;
        return null;
    }

    public Integer getWins() {
        return wins;
    }

    public void setWins(Integer wins) {
        this.wins = wins;
    }

    public Integer getLoses() {
        return loses;
    }

    public void setLoses(Integer loses) {
        this.loses = loses;
    }

    public Integer getRacksWon() {
        return racksWon;
    }

    public void setRacksWon(Integer racksWon) {
        this.racksWon = racksWon;
    }

    public Integer getRacksLost() {
        return racksLost;
    }

    public void setRacksLost(Integer racksLost) {
        this.racksLost = racksLost;
    }

    public Integer getMatches() {
        return matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @JsonView(PlayerResultSummary.class)
    public Integer getSetLoses() {
        return setLoses;
    }

    public void setSetLoses(Integer setLoses) {
        this.setLoses = setLoses;
    }

    @JsonView(PlayerResultSummary.class)
    public Integer getSetWins() {
        return setWins;
    }

    public void setSetWins(Integer setWins) {
        this.setWins = setWins;
    }

    @JsonView(PlayerResultSummary.class)
    public Double getRackPct() {
        if (matches == 0) {
            return 0d;
        }
        return (double)racksWon/((double) racksWon+racksLost);
    }

    public void setSeason(Season season) {
        this.season = season;
    }

    public Double getPoints() {
        return points;
    }

    public void setPoints(Double points) {
        this.points = points;
    }

    @JsonView(PlayerResultSummary.class)
    public Double getWinPct() {
        if (matches == 0) {
            return 0d;
        }

        return (double)wins/((double) wins+loses);
    }

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }


    @JsonIgnore
    public StatSeason getStatSeason() {
        return new StatSeason(getSeason().getsDate().getYear(), getSeason().getSeasonType());
    }

    public static Comparator<Stat> sortSeasonStats() {
        return (o1, o2) -> o2.getPoints().compareTo(o1.getPoints());
    }

    public static <T extends Stat> Comparator<T> sortUserStats() {
        return new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                if (!o1.getPoints().equals(o2.getPoints()))
                    return  o2.getPoints().compareTo(o1.getPoints());

                if (o1.getSeason().isNine())
                    return o2.getRackPct().compareTo(o1.getRackPct());

                return o2.getWinPct().compareTo(o1.getWinPct());
            }
        };
    }

    public static List<Team> sortTeamStats(List<Team> stats) {
        stats.sort(new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                if (!o1.getStats().getWins().equals(o2.getStats().getWins())) {
                    return o2.getStats().getWins().compareTo(o1.getStats().getWins());
                }
                return o2.getStats().getRackPct().compareTo(o1.getStats().getRackPct());
            }
        });
        int rank = 0;
        for (Team stat : stats) {
            stat.setRank(++rank);
        }
        return stats;
    }
}
