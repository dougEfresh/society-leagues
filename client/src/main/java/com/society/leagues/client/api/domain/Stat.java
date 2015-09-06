package com.society.leagues.client.api.domain;


import java.util.ArrayList;
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
    Team team;
    User User;

    public Stat() {
    }

    public static Stat buildTeamStats(final Team team, final List<TeamMatch> matches) {
        Stat s = new Stat();
        s.setTeam(team);
        List<TeamMatch> results = matches.stream().filter(tm->tm.hasResults()).collect(Collectors.toList());
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
        }
        return s;
    }

    public static List<Stat> buildTeamMemberStats(final Team team, final List<PlayerResult> matches) {
        List<Stat> stats = new ArrayList<>();
        for (User user : team.getMembers()) {
            Stat s = new Stat();
            s.setTeam(team);
            s.setUser(user);
            for (PlayerResult match : matches) {
                if (!match.hasUser(user)) {
                    continue;
                }
                s.matches++;
                if (match.isWinner(user)) {
                    s.wins++;
                    s.racksWon += match.getWinnerRacks();
                    s.racksLost += match.getLoserRacks();
                } else {
                    s.loses++;
                    s.racksWon += match.getLoserRacks();
                    s.racksLost += match.getWinnerRacks();
                }
            }
            stats.add(s);
        }
        return stats;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }

    public User getUser() {
        return User;
    }

    public void setUser(User user) {
        User = user;
    }

    public Season getSeason() {
        return team != null ? team.getSeason() : null;
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

    public Double getWinPct() {
        if (matches == 0) {
            return 0d;
        }

        return (double)wins/((double) wins+loses);
    }
}
