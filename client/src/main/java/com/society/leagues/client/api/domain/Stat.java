package com.society.leagues.client.api.domain;


import java.util.ArrayList;
import java.util.Collections;
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
    StatType type;
    Team team;
    User user;
    Season season;
    Handicap handicap;

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

    public static Stat buildPlayerTeamStats(final User u, final Team team , final List<PlayerResult> matches) {
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

    public static Stat buildPlayerSeasonStats(final User u, final Season season , final List<PlayerResult> matches) {
        Stat s = new Stat();
        s.setUser(u);
        s.setSeason(season);
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

    public String getHandicap() {
        if (handicap != null) {
            return Handicap.format(handicap);
        }
        if (user == null || team == null) {
            return "";
        }
        HandicapSeason handicapSeason = user.getHandicapSeasons().stream().filter(hs->hs.getSeason().equals(team.getSeason())).findFirst().orElse(null);
        return handicapSeason == null ? "" : handicapSeason.getHandicapDisplay();
    }

    private static List<Stat> buildHandicapStats(final User u, final List<PlayerResult> matches) {
        Stat s = new Stat();
        s.setUser(u);
        s.type = StatType.HANDICAP;
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
        return Collections.EMPTY_LIST;
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

        if (user != null && team != null) {
            return StatType.USER_SEASON;
        }

        if (user == null && team != null) {
            return StatType.TEAM;
        }

        return StatType.UNKNOWN;
    }

    public Season getSeason() {
        if (season != null)
            return season;
        if (team != null) {
            return team.getSeason();
        }
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

    public void setSeason(Season season) {
        this.season = season;
    }




    public Double getWinPct() {
        if (matches == 0) {
            return 0d;
        }

        return (double)wins/((double) wins+loses);
    }
}
