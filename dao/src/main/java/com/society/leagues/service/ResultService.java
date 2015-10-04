package com.society.leagues.service;

import com.society.leagues.client.api.domain.MatchPoints;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class ResultService {
    @Autowired LeagueService leagueService;
    public static double period = 10;
    List<MatchPoints> matchPointsCache = new ArrayList<>();

    @PostConstruct
    public void init() {
        refresh();
    }

    @Scheduled(fixedRate = 1000*60*6, initialDelay = 1000*60*11)
    public void refresh() {
        matchPoints().clear();
        List<PlayerResult> challengeResults = leagueService.findCurrent(PlayerResult.class).stream().parallel()
                .filter(r -> r.getSeason().isChallenge())
                .collect(Collectors.toList());

        for(User challengeUser: leagueService.findAll(User.class).stream()
                .filter(u -> u.isChallenge())
                //.filter(u->u.getLastName().equals("Chimento"))
                .collect(Collectors.toList())
                ) {
            List<PlayerResult> results = challengeResults.stream().parallel().filter(pr->pr.hasUser(challengeUser)).
                    sorted((playerResult, t1) -> t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate())).limit(7)
                    .collect(Collectors.toList());

            double matchNum = 0;
            for (PlayerResult challengeResult : results) {
                MatchPoints mp = new MatchPoints();
                int points = 1;
                if (challengeResult.isWinner(challengeUser)) {
                    points += 2;
                } else {
                    points += challengeResult.getWinnerRacks()-challengeResult.getLoserRacks() == 1 ? 1 : 0;
                }
                mp.setPoints(points);
                mp.setWeightedAvg((double) points / (period / (period - matchNum)));
                mp.setMatchNum(new Double(matchNum).intValue());
                mp.setPlayerResult(challengeResult);
                mp.setUser(challengeUser);
                mp.setCalculation(String.format("(%s * (10-%s))/10", points, new Double(matchNum).intValue()));
                matchNum++;
                matchPointsCache.add(mp);

            }
        }

    }

    public List<MatchPoints> matchPoints() {
        return matchPointsCache;
    }

    public PlayerResult createOrModify(PlayerResult playerResult) {
        if (playerResult.getSeason().isChallenge()) {
            TeamMatch tm = playerResult.getTeamMatch();
            tm.setAwayRacks(playerResult.getAwayRacks());
            tm.setHomeRacks(playerResult.getHomeRacks());
            tm.setSetAwayWins(playerResult.getAwayRacks());
            tm.setSetHomeWins(playerResult.getHomeRacks());
            leagueService.save(tm);
        }

        return leagueService.save(playerResult);

    }
}
