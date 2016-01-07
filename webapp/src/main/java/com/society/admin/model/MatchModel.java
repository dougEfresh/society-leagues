package com.society.admin.model;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.PlayerResult;
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

    public int getHomeCumulativeHC() {
        HashSet<Handicap> homeHandicaps = new HashSet<>();
        List<PlayerResult> results = playerResults.stream().sorted(new Comparator<PlayerResult>() {
            @Override
            public int compare(PlayerResult o1, PlayerResult o2) {
                try {
                    Integer.parseInt(o1.getPlayerHomeHandicap().getDisplayName());
                    Integer.parseInt(o2.getPlayerHomeHandicap().getDisplayName());
                } catch (NumberFormatException e) {
                    logger.warn(e.getMessage());
                    return -1;
                }
                return new Integer(o2.getPlayerHomeHandicap().getDisplayName()).compareTo(new Integer(o1.getPlayerHomeHandicap().getDisplayName()));
            }
        }).limit(4).collect(Collectors.toList());
        for (PlayerResult result : results) {
            homeHandicaps.add(result.getPlayerHomeHandicap());
        }

        int hc = 0;
        for (Handicap homeHandicap : homeHandicaps) {
            hc += new Integer(homeHandicap.getDisplayName());
        }
        return hc;
    }


    public List<PlayerResult> getPlayerResults() {
        return playerResults;
    }

    public void setPlayerResults(List<PlayerResult> playerResults) {
        this.playerResults = playerResults;
    }
}
