package com.society.leagues.client.api.domain;


import java.util.Comparator;
import java.util.List;

public class StatSorter {

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
