package com.society.leagues.service;

import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
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
        refresh();
        scrambleGameType();
    }

    public Collection<PlayerResult> createNewPlayerResults(TeamMatch teamMatch) {
        List<PlayerResult> playerResults = new ArrayList<>();
        int matches = teamMatch.getHomeRacks() + teamMatch.getAwayRacks();
        if (teamMatch.getSeason().isNine() && !teamMatch.getSeason().isChallenge()) {
            matches = teamMatch.getSetHomeWins() + teamMatch.getSetAwayWins();
        }
        User[] homeMembers = teamMatch.getHome().getMembers().toArray(new User[]{});
        User[] awayMembers = teamMatch.getAway().getMembers().toArray(new User[]{});
        for(int i = 0 ; i<matches; i++) {
            PlayerResult r = new PlayerResult(homeMembers[i % 4],awayMembers[i % 4],teamMatch);
            r.setMatchNumber(i+1);
            playerResults.add(r);
        }
        if (playerResults.isEmpty())
            return playerResults;

        leagueService.save(playerResults);
        return leagueService.findAll(PlayerResult.class).parallelStream().filter(p->p.getTeamMatch().equals(teamMatch)).collect(Collectors.toList());
    }

    public Collection<PlayerResult> createNewPlayerMatchResultsNine(TeamMatch teamMatch) {
        List<PlayerResult> playerResults = new ArrayList<>();
        int matches = teamMatch.getSetHomeWins() + teamMatch.getSetAwayWins();
        User[] homeMembers = teamMatch.getHome().getMembers().toArray(new User[]{});
        User[] awayMembers = teamMatch.getHome().getMembers().toArray(new User[]{});
        for(int i = 0 ; i<matches; i++) {
            PlayerResult r = new PlayerResult(homeMembers[i],awayMembers[i],teamMatch);
            r.setMatchNumber(i+1);
            playerResults.add(r);
        }
        return playerResults;
    }

    public void scrambleGameType() {
        Map<LocalDate,List<TeamMatch>> matches = leagueService.findCurrent(TeamMatch.class).parallelStream()
                .filter(tm -> tm.getSeason().isScramble())
                .filter(tm -> tm.getDivision() == Division.MIXED_MONDAYS_MIXED)
                .collect(Collectors.groupingBy(
                        tm -> tm.getMatchDate().toLocalDate()
                ));
        Division division = Division.MIXED_EIGHT;
        for (LocalDate localDate : matches.keySet().stream().sorted(new Comparator<LocalDate>() {
            @Override
            public int compare(LocalDate o1, LocalDate o2) {
                return o1.compareTo(o2);
            }
        }).collect(Collectors.toList())) {

            for (TeamMatch teamMatch : matches.get(localDate)) {
                teamMatch.setDivision(division);
                leagueService.save(teamMatch);
            }
            division = division == Division.MIXED_EIGHT ? Division.MIXED_NINE : Division.MIXED_EIGHT;
        }
    }

//    @Scheduled(fixedRate = 1000*60*6, initialDelay = 1000*60*11)
    public void refresh() {
        matchPoints().clear();
        LocalDateTime tenWeeks = LocalDateTime.now().plusDays(1).minusWeeks(10);
        List<PlayerResult> challengeResults = leagueService.findCurrent(PlayerResult.class).stream().parallel()
                .filter(r -> r.getSeason().isChallenge())
                .filter(pr -> pr.getMatchDate().isAfter(tenWeeks))
                .collect(Collectors.toList());

        for(User challengeUser: leagueService.findAll(User.class).stream()
                .filter(u -> u.isChallenge())
                .collect(Collectors.toList())
                ) {
            List<PlayerResult> results = challengeResults.stream().parallel()
                    .filter(pr -> pr.hasUser(challengeUser))
                    .sorted((playerResult, t1) -> t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate())).limit(7)
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

    public List<PlayerResult> createOrModify(List<PlayerResult> playerResult) {
        List<PlayerResult> returned = new ArrayList<>(playerResult.size());
        for (PlayerResult result : playerResult) {
            PlayerResult existing =  leagueService.findOne(result);
            if (existing == null) {
                existing = new PlayerResult();
                existing.setTeamMatch(leagueService.findOne(result.getTeamMatch()));;
            }
            existing.setPlayerHome(leagueService.findOne(result.getPlayerHome()));
            existing.setPlayerAway(leagueService.findOne(result.getPlayerAway()));
            existing.setHomeRacks(result.getHomeRacks());
            existing.setAwayRacks(result.getAwayRacks());
            existing.setPlayerHomeHandicap(existing.getPlayerHome().getHandicap(existing.getSeason()));
            existing.setPlayerAwayHandicap(existing.getPlayerAway().getHandicap(existing.getSeason()));
            existing.setPlayerHomePartner(leagueService.findOne(result.getPlayerHomePartner()));
            existing.setPlayerAwayPartner(leagueService.findOne(result.getPlayerAwayPartner()));
            existing.setMatchNumber(result.getMatchNumber());
            if (existing.getPlayerHomePartner() != null)
                existing.setPlayerHomeHandicapPartner(result.getPlayerHomePartner().getHandicap(existing.getSeason()));

            if (existing.getPlayerAwayPartner() != null)
                existing.setPlayerAwayHandicapPartner(result.getPlayerAwayPartner().getHandicap(existing.getSeason()));

            returned.add(existing);
        }
        leagueService.save(returned);
        return returned;
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

    public PlayerResult add(TeamMatch teamMatch) {
        List<PlayerResult> results = leagueService.findCurrent(PlayerResult.class)
                .parallelStream()
                .filter(pr -> pr.getTeamMatch().equals(teamMatch))
                .collect(Collectors.toList());
        int matchNumber = 1;
        if (!results.isEmpty()) {
            matchNumber = results.stream().max(new Comparator<PlayerResult>() {
                @Override
                public int compare(PlayerResult o1, PlayerResult o2) {
                    return o1.getMatchNumber().compareTo(o2.getMatchNumber());
                }
            }).get().getMatchNumber() +1;
        }
        PlayerResult result = new PlayerResult();
        result.setMatchNumber(matchNumber);
        result.setPlayerHome(teamMatch.getHome().getTeamMembers().getMembers().iterator().next());
        result.setPlayerAway(teamMatch.getAway().getTeamMembers().getMembers().iterator().next());
        result.setPlayerHomeHandicap(result.getPlayerHome().getHandicap(teamMatch.getSeason()));
        result.setPlayerAwayHandicap(result.getPlayerHome().getHandicap(teamMatch.getSeason()));
        result.setTeamMatch(teamMatch);
        return leagueService.save(result);
    }

    public Boolean removeTeamMatchResult(TeamMatch teamMatch) {
        //Remove player results;
        if (teamMatch == null)
            return false;

        List<PlayerResult> toDelete = leagueService.findAll(PlayerResult.class).parallelStream().filter(
                r -> r.getTeamMatch() != null && r.getTeamMatch().equals(teamMatch)).collect(Collectors.toList());

        for (PlayerResult result : toDelete) {
            leagueService.purge(result);
        }
        leagueService.purge(teamMatch);
        return true;
    }

}
