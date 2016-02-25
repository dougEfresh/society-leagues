package com.society.leagues.model;

import com.society.leagues.client.api.domain.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SuppressWarnings("unused")
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

    public List<User> getAvailable(Team team) {
        if (getHome().equals(team)) {
            return homeAvailable();
        }
        return awayAvailable();
    }

    public List<User> getNotAvailable(Team team) {
        if (getHome().equals(team)) {
            return homeNotAvailable();
        }
        return awayNotAvailable();
    }

    final static Comparator<User> sort = (o1, o2) -> o1.getFirstName().compareTo(o2.getFirstName());

    public List<User> getMembers(Team team) {
        return getHome().equals(team) ? getHome().getMembers().getMembers().stream().sorted(sort).collect(Collectors.toList())
                : getAway().getMembers().getMembers().stream().sorted(sort).collect(Collectors.toList());
    }

    public boolean isAvailable(User user) {
        if (getPlayerResults().isEmpty())
            return awayAvailable().contains(user) || homeAvailable().contains(user);

        return getPlayerResults().stream().filter(p->p.hasUser(user)).count() > 0;
    }

    public boolean isPlayedOrAvailable(User user) {
        if (getPlayerResults().stream().filter(pr->pr.hasUser(user)).count() > 0)
            return true;

        if (getMatchDate().isBefore(LocalDateTime.now().minusDays(1)))
            return false;

        return isAvailable(user);
    }

    @Override
    public Integer getWinnerRacks() {
        return racks(getWinner());
    }

    @Override
    public Integer getLoserRacks() {
        return racks(getLoser());
    }

    public int racks(Team t) {
        int racks = getHome().equals(t) ? getHomeForfeits() : getAwayForfeits();
        boolean isHome = getHome().equals(t);

        for (PlayerResult playerResult : playerResults) {
            racks += isHome ? playerResult.getHomeRacks() : playerResult.getAwayRacks();
        }
        return racks;
    }

    public int forfeits(Team t) {
       return getHome().equals(t) ? getHomeForfeits() : getAwayForfeits();
   }

    public Team getOpponent(User user) {
        return getHome().hasUser(user) ? getAway() : getHome();
    }

    public int getHomeCumulativeHC() {
        if (!hasPlayerResults() || getSeason().isNine() || getSeason().isChallenge())
            return 0;

        return 0;
    }

    public int getAwayCumulativeHC() {
        if (!hasPlayerResults() || getSeason().isNine() || getSeason().isChallenge())
            return 0;

        return 0;

    }

    public boolean isWin(Team team) {
        return getWinner().equals(team);
    }

    public boolean hasPlayerResults() {
        return playerResults.stream().filter(PlayerResult::hasResults).count() > 0;
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
