package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
public class UserStats {
    @JsonIgnore
    List<PlayerStats> playerStatsList;
    @JsonIgnore
    HashMap<Handicap,List<PlayerStats>> handicapStats = new HashMap<>();

    public Stats getAll() {
        return getStat(playerStats -> true, playerStatsList);
    }

    public Map<DivisionType,Stats> getByDivision() {
        Map<DivisionType,Stats> divisionStats = new HashMap<>();
        divisionStats.put(DivisionType.EIGHT_BALL_CHALLENGE, getStat(
                        playerStats -> playerStats.getPlayer().getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE,
                        playerStatsList
                )
        );
        divisionStats.put(DivisionType.NINE_BALL_CHALLENGE, getStat(
                        playerStats -> playerStats.getPlayer().getDivision().getType() == DivisionType.NINE_BALL_CHALLENGE,
                        playerStatsList
                )
        );

        return divisionStats;
    }


    public Map<Handicap,Stats> getByHandicap() {
        Map<Handicap,Stats> statsMap = new HashMap<>();
        for (final Handicap handicap : handicapStats.keySet()) {
            statsMap.put(handicap, getStat(playerStats -> true,handicapStats.get(handicap)));
        }
        Map<Handicap,Stats> nonEmpty = new HashMap<>();
        for (Handicap handicap : statsMap.keySet()) {
            if (statsMap.get(handicap).getMatches() != 0) {
                nonEmpty.put(handicap,statsMap.get(handicap));
            }
        }
        return nonEmpty;
    }

    @JsonIgnore
    private Stats getStat(Predicate<PlayerStats> predicate, List<PlayerStats> playerStatsList) {
        final Stats s = new Stats();
        List <PlayerStats> ps = playerStatsList.stream().filter(predicate).collect(Collectors.toList());
        if (ps == null || ps.isEmpty()) {
            return s;
        }
        for(PlayerStats p: ps) {
            s.addLoses(p.getLoses());
            s.addWins(p.getWins());
            s.addMatches(p.getMatches());
            s.addPoints(p.getPoints());
            s.addRacks(p.getRacksWon());
            s.addRacksLost(p.getRacksLost());
        }
        return s;
    }

    public void setPlayerStatsList(List<PlayerStats> playerStatsList) {
        this.playerStatsList = playerStatsList;
    }

    public void addHandicapStats(Handicap handicap, List<PlayerStats> playerStatsList) {
        handicapStats.put(handicap, playerStatsList);
    }
}
