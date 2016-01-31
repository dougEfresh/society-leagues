package com.society.leagues.client.api.domain;

import java.util.List;
import java.util.stream.Collectors;

public class StatBuilder {

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


    public static Stat buildPlayerSeasonStats(final User u, final Season season , final List<PlayerResult> matches) {
        Stat s = new Stat();
        s.setUser(u);
        s.setSeason(season);
        calculate(u,s,matches);
        return s;
    }

}
