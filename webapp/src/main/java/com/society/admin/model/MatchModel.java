package com.society.admin.model;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class MatchModel extends TeamMatch {
    static Logger logger = LoggerFactory.getLogger(MatchModel.class);
    List<PlayerResult> playerResults = new ArrayList<>();

    public static List<MatchModel> fromTeam(List<TeamMatch> teamMatches) {
        List<MatchModel> matchModels = new ArrayList<>(teamMatches.size());

        for (TeamMatch teamMatch : teamMatches) {
            MatchModel m = new MatchModel();
            ReflectionUtils.shallowCopyFieldState(teamMatch,m);
            matchModels.add(m);
        }

        return matchModels;
    }

    public int getHc(Team t) {
        return getHome().equals(t) ? getHomeCumulativeHC() : getAwayCumulativeHC();
    }

    public int getOpponentHc(Team t) {
        return getHome().equals(t) ? getAwayCumulativeHC() : getHomeCumulativeHC();
    }

    public int racks(Team t) {
        return getHome().equals(t) ? getHomeRacks() : getAwayRacks();
    }

   public int forfeits(Team t) {
       return getHome().equals(t) ? getHomeForfeits() : getAwayForfeits();
   }

    public int getHomeCumulativeHC() {
        if (!hasPlayerResults() || getSeason().isNine() || getSeason().isChallenge())
            return 0;

        return 0;
        /*
        HashSet<Player> handicaps = new HashSet<>();
        playerResults.stream().forEach(p->handicaps.add(new Player(p.getPlayerHome().getId(),p.getPlayerHomeHandicap())));
        List<Player> players = new ArrayList<>();
        handicaps.stream().sorted(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return new Integer(o2.handicap.ordinal()).compareTo(new Integer(o1.handicap.ordinal()));
            }
        }).limit(4).forEach(players::add);

        if (players.size() < 4) {
            logger.error(String.format("Found less than 4 players %s %s",getSeason().getId(), getId()));
        }
        int hc = 0;
        for (Player player : players) {
            hc += new Integer(player.handicap.getDisplayName());
        }

        return hc;
        */
    }

    public int getAwayCumulativeHC() {
        if (!hasPlayerResults() || getSeason().isNine() || getSeason().isChallenge())
            return 0;

        return 0;
        /*

        HashSet<Player> handicaps = new HashSet<>();

        playerResults.stream().forEach(p->handicaps.add(new Player(p.getPlayerAway().getId(),p.getPlayerAwayHandicap())));
        List<Player> players = new ArrayList<>();
        handicaps.stream().sorted(new Comparator<Player>() {
            @Override
            public int compare(Player o1, Player o2) {
                return new Integer(o2.handicap.ordinal()).compareTo(new Integer(o1.handicap.ordinal()));
            }
        }).limit(4).forEach(players::add);

        if (players.size() < 4) {
            logger.error(String.format("Found less than 4 players %s %s",getSeason().getId(), getId()));
        }
        int hc = 0;
        for (Player player : players) {
            hc += new Integer(player.handicap.getDisplayName());
        }
        return hc;
        */
    }

    public boolean isWin(Team team) {
        return getWinner().equals(team);
    }

    public boolean hasPlayerResults() {
        return playerResults.stream().filter(p->p.hasResults()).count() > 0;
    }

    static class Player {
        final String id;
        final Handicap handicap;

        public Player(String id, Handicap handicap) {
            this.handicap = handicap;
            this.id = id;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Player)) return false;

            Player player = (Player) o;

            if (!id.equals(player.id)) return false;

            return true;
        }

        @Override
        public int hashCode() {
            return id.hashCode();
        }
    }

    public String homeOrAway(Team team) {
        return getHome().equals(team) ? "H" : "A";
    }

    public Team getOpponent(Team team) {
        return getHome().equals(team) ? getAway() : getHome();
    }

    public List<PlayerResult> getPlayerResults() {
        return playerResults;
    }

    public void setPlayerResults(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }
}
