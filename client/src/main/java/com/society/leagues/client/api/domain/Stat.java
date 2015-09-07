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
    String type;
    Team team;
    User user;

    public Stat() {
    }

    public static Stat buildTeamStats(final Team team, final List<TeamMatch> matches) {
        Stat s = new Stat();
        s.setTeam(team);
        List<TeamMatch> results = matches.stream().filter(TeamMatch::hasResults).collect(Collectors.toList());
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

    public static Stat buildPlayerTeamStats(final User u, Team team , final List<PlayerResult> matches) {
        Stat s = new Stat();
        s.setUser(u);
        s.setTeam(team);
        for (PlayerResult match : matches) {
            if (!match.hasUser(u)) {
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
        return s;
    }


    public static Stat buildStats(final User u, final List<Stat> stats) {
        Stat s = new Stat();
        s.setUser(u);
        s.type = "all";
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


    public String getType() {
        if (type != null) {
            return type;
        }

        if (user != null && team != null) {
            return "season";
        }

        if (user == null && team != null) {
            return "team";
        }

        return "unknown";
    }

    public void setType(String type) {
        this.type = type;
    }

    public Season getSeason() {
        if (team == null) {
            return null;
        }
        return team.getSeason();
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

    public Double getWinPct() {
        if (matches == 0) {
            return 0d;
        }

        return (double)wins/((double) wins+loses);
    }
}
