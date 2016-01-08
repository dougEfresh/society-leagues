package com.society.leagues.client.api.domain;

import java.util.List;
import java.util.stream.Collectors;

public class Stat {
    Integer wins = 0;
    Integer loses = 0;
    Integer racksWon = 0;
    Integer racksLost = 0;
    Integer setWins = 0;
    Integer setLoses = 0;
    Integer matches = 0;
    Integer forfeits = 0;
    StatType type;
    User user;
    Season season;
    Handicap handicap;
    Double points = 0d;
    Integer rank = 0;
    Team team;

    public Stat() {
    }


    public void setType(StatType type) {
        this.type = type;
    }

    public String getGame() {
        return getType() == StatType.MIXED_EIGHT ? "8" : "9";
    }
    public static Stat buildHandicapStats(final List<PlayerResult> results, StatType statType, User user, Handicap handicap) {
        Stat s= new Stat();
        if (results == null || results.isEmpty())
            return null;

        s.setSeason(results.get(0).getSeason());
        s.setUser(user);
        s.setHandicap(handicap);
        calculate(user,s,results);
        return s;
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

    public static Stat buildPlayerTeamStats(final User u, final Team team , final List<PlayerResult> matches) {
        Stat s = new Stat();
        s.setUser(u);
        s.setSeason(team.getSeason());
        calculate(u,s,matches);
        return s;
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

    public Handicap getHandicap() {
        if (handicap == null) {
            return Handicap.NA;
        }
        return handicap;
    }

    public boolean isLifeTime() {
        return getType().isLifetime();
    }
    public String getHandicapDisplay() {
        if (getType().isLifetime())
            return "";
        return getHandicap().getDisplayName();
    }

    public static Stat buildLifeTimeStats(final User u, final List<Stat> stats) {
        Stat s = new Stat();
        s.setUser(u);
        s.type = StatType.ALL;
        for (Stat stat : stats) {
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

    public Integer getSetLoses() {
        return setLoses;
    }

    public void setSetLoses(Integer setLoses) {
        this.setLoses = setLoses;
    }

    public Integer getSetWins() {
        return setWins;
    }

    public void setSetWins(Integer setWins) {
        this.setWins = setWins;
    }

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

    public static boolean isDifferent(Stat s1, Stat s2) {
        if (s1 == null || s2 == null)
            return true;

        return !(s1.getLoses().equals(s2.getLoses()) &&
                        s1.getWins().equals(s2.getWins()) &&
                        s1.getRacksLost().equals(s2.getRacksLost()) &&
                        s1.getRacksWon().equals(s2.getRacksWon()) &&
                        s1.getSetLoses().equals(s2.getSetLoses()) &&
                        s1.getSetWins().equals(s2.getSetWins()));
    }
}
