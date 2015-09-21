package com.society.leagues.Service;

import com.society.leagues.client.api.domain.MatchPoints;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Component
public class ResultService {
    @Autowired LeagueService leagueService;
    public static double period = 10;
    List<MatchPoints> matchPointsCache = new ArrayList<>();

    @PostConstruct
    public void init() {

        //refresh();
    }

    @Scheduled(fixedRate = 1000*60*6, initialDelay = 1000*60*11)
    public void refresh() {
        List<PlayerResult> challengeResults = leagueService.findAll(PlayerResult.class).stream().parallel()
                .filter(r -> r.getSeason().isChallenge())
                .collect(Collectors.toList());

        Map<User,List<PlayerResult>> homeResults = challengeResults.parallelStream().collect(Collectors.groupingBy(PlayerResult::getPlayerHome));
        Map<User,List<PlayerResult>> awayResults = challengeResults.parallelStream().collect(Collectors.groupingBy(PlayerResult::getPlayerAway));
        Map<User,List<PlayerResult>> allResults = new HashMap<User,List<PlayerResult>>(5000);
        for (User user : homeResults.keySet()) {
            if (!allResults.containsKey(user)) {
                allResults.put(user,new ArrayList<>(100));
            }
            allResults.get(user).addAll(homeResults.get(user));
        }

        for (User user : awayResults.keySet()) {
            if (!allResults.containsKey(user)) {
                allResults.put(user,new ArrayList<>(100));
            }
            allResults.get(user).addAll(awayResults.get(user));
        }

        for (User user : allResults.keySet()) {
            List<PlayerResult> results = allResults.get(user).stream().sorted(new Comparator<PlayerResult>() {
                @Override
                public int compare(PlayerResult playerResult, PlayerResult t1) {
                    return t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate());
                }
            }).limit(7).collect(Collectors.toList());
            double matchNum = 0;

            for (PlayerResult challengeResult : results) {
                MatchPoints mp = new MatchPoints();
                int points = 1;
                if (challengeResult.isWinner(user)) {
                    points += 2;
                } else {
                    points += challengeResult.getWinnerRacks()-challengeResult.getLoserRacks() == 1 ? 1 : 0;
                }
                mp.setCalculation(String.format("%s/(%s/%s)",points,period,period-matchNum));
                mp.setPoints(points);
                mp.setMatchNum(new Double(matchNum).intValue());
                mp.setWeightedAvg((double) points / (period / (period - matchNum)));
                mp.setPlayerResult(challengeResult);
                matchPointsCache.add(mp);
            }
        }
        LocalDateTime tenWeeksAgo = LocalDateTime.now().minusWeeks(10);
        for(MatchPoints p: matchPointsCache.stream().
                filter(r -> r.getPlayerResult().getMatchDate()
                        .isBefore(tenWeeksAgo)).collect(Collectors.toList())) {
            matchPointsCache.remove(p);
        }
    }

    public List<MatchPoints> matchPoints() {
        return matchPointsCache;
    }

    public PlayerResult createOrModify(PlayerResult playerResult) {

        if (playerResult.getSeason().getDivision().isChallenge()) {
            TeamMatch tm = playerResult.getTeamMatch();
            tm.setAwayRacks(playerResult.getAwayRacks());
            tm.setHomeRacks(playerResult.getHomeRacks());
            tm.setSetAwayWins(playerResult.getAwayRacks());
            tm.setSetHomeWins(playerResult.getHomeRacks());
            leagueService.save(tm);
        }

        return leagueService.save(playerResult);

    }

     public TeamMatch createOrModify(TeamMatch teamMatch) {

        if (teamMatch.getSeason().getDivision().isChallenge()) {
            // don't modify team matches for challenges
            return  teamMatch;
        }

        return leagueService.save(teamMatch);

    }
}
