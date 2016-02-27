package com.society.leagues.service;

import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class ResultService {
    @Autowired LeagueService leagueService;
    public static double period = 10;
    List<MatchPoints> matchPointsCache = new ArrayList<>();

    @PostConstruct
    @Scheduled(fixedRate = 1000*60*10, initialDelay = 1000*60*3)
    public void init() {
        refresh();
    }

    public Collection<PlayerResult> createNewPlayerResults(TeamMatch teamMatch) {
        List<PlayerResult> playerResults = new ArrayList<>();
        User[] homeMembers = teamMatch.getHome().getMembers().getMembers().toArray(new User[]{});
        User[] awayMembers = teamMatch.getAway().getMembers().getMembers().toArray(new User[]{});
        for(int i = 0 ; i<4; i++) {
            PlayerResult r = new PlayerResult(homeMembers[i % 4],awayMembers[i % 4],teamMatch);
            r.setMatchNumber(i+1);
            if (teamMatch.getSeason().isScramble() && i == 3) {
                r.setMatchNumber(5);
            }
            playerResults.add(r);
        }
        if (playerResults.isEmpty())
            return playerResults;

        leagueService.save(playerResults);
        return leagueService.findAll(PlayerResult.class).parallelStream().filter(p->p.getTeamMatch().equals(teamMatch)).collect(Collectors.toList());
    }

    public void refresh() {
        matchPoints().clear();
        LocalDateTime tenWeeks = LocalDateTime.now().plusDays(1).minusWeeks(10);
        List<PlayerResult> allResults =
                leagueService.findAll(PlayerResult.class).stream().parallel()
                        .filter(r->r.getTeamMatch() != null)
                        .filter(r->r.getSeason() != null)
                        .filter(r->r.getSeason().isActive())
                        .filter(pr -> pr.getMatchDate() != null)
                        .filter(PlayerResult::hasResults)
                        .collect(Collectors.toList());
        Season nineSeason = leagueService.findAll(Season.class).stream().filter(Season::isActive).filter(s->s.getDivision() == Division.NINE_BALL_TUESDAYS).findAny().orElse(null);
        Stream<User> users = leagueService.findAll(User.class).stream();
        Stream<User> challengeUsers = users.filter(User::isChallenge);
        Stream<User> nineUsers = users.filter(u->u.hasSeason(nineSeason));
        Stream<User> eightUsers =users.filter(u->u.getSeasons().stream().filter(s->s.getDivision().isEight()).count() > 0);

        challengeUsers.forEach(user -> {
            List<PlayerResult> results = allResults.stream().parallel()
                    .filter(r -> r.getSeason().isChallenge())
                    .filter(pr -> pr.hasUser(user)).filter(PlayerResult::hasResults)
                    .filter(pr -> pr.getMatchDate().isAfter(tenWeeks))
                    .sorted((playerResult, t1) -> t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate())).limit(7)
                    .collect(Collectors.toList());
            calcPoints(user, results,user.getSeasons().stream().filter(Season::isChallenge).findAny().get());
        });

        nineUsers.forEach(user-> {
            List<PlayerResult> results = allResults.stream().parallel()
                    .filter(pr -> pr.hasUser(user))
                    .filter(pr -> pr.getSeason().equals(nineSeason))
                    .sorted((playerResult, t1) -> t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate()))
                    .collect(Collectors.toList());
            calcPoints(user, results, nineSeason);
        });
        /*
        eightUsers.forEach(user-> {
            List<PlayerResult> results = allResults.stream().parallel()
                    .filter(pr -> pr.hasUser(user))
                    .filter(pr -> pr.getSeason().equals(nineSeason))
                    .sorted((playerResult, t1) -> t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate()))
                    .collect(Collectors.toList());
            calcPoints(user, results, nineSeason);
        });
        */
    }

    private void calcPoints(User user, List<PlayerResult> results, Season season) {
        double matchNum = 0;
        for (PlayerResult challengeResult : results) {
            MatchPoints mp = new MatchPoints();
            int points = 1;
            String[] r = challengeResult.getRace().split("/");
            int handciapGames = 0;
            if (r.length == 0) {
                handciapGames = 0;
            } else {
                try {
                    handciapGames = Integer.parseInt(r[0]);
                } catch (NumberFormatException e) {
                }
            }
            if (challengeResult.isWinner(user)) {
                if (challengeResult.getLoserRacks() == 0) {
                    points += 1;
                } else {
                    if (challengeResult.getLoserHandicap().ordinal() < challengeResult.getWinnerHandicap().ordinal()) {
                        if (challengeResult.getLoserRacks() - handciapGames <= 0)
                            points += 1;
                    }
                }
                points += 2;
            } else {
                points += challengeResult.getWinnerRacks()-challengeResult.getLoserRacks() == 1 ? 1 : 0;
            }

            mp.setPoints(points);
            mp.setMatchNum(new Double(matchNum).intValue());
            mp.setPlayerResult(challengeResult);
            mp.setUser(user);
            if (season.isChallenge()) {
                mp.setWeightedAvg((double) points / (period / (period - matchNum)));
                mp.setCalculation(String.format("(%s * (10-%s))/10", points, new Double(matchNum).intValue()));
            }
            matchNum++;
            matchPointsCache.add(mp);
        }
    }

    public List<MatchPoints> matchPoints() {
        return matchPointsCache;
    }

    public List<PlayerResult> createOrModify(List<PlayerResult> playerResult) {
        List<PlayerResult> returned = new ArrayList<>(playerResult.size());
        for (PlayerResult result : playerResult) {
            if (result.getPlayerAway().getId().equals("-1") || Objects.equals(result.getPlayerHome().getId(), "-1")) {
                returned.add(result);
                continue;
            }
            PlayerResult existing =  leagueService.findOne(result);
            if (existing == null) {
                existing = new PlayerResult();
                existing.setTeamMatch(leagueService.findOne(result.getTeamMatch()));;
            }
            User u = leagueService.findOne(result.getPlayerHome());
            if (u == null)
                u = User.defaultUser();
            existing.setPlayerHome(u);

            u = leagueService.findOne(result.getPlayerAway());
            if (u == null)
                u = User.defaultUser();
            existing.setPlayerAway(u);
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

    public PlayerResult add(TeamMatch teamMatch) {
        List<PlayerResult> results = leagueService.findAll(PlayerResult.class)
                .parallelStream()
                .filter(pr -> pr.getSeason().isActive())
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
        result.setPlayerHome(teamMatch.getHome().getMembers().getMembers().iterator().next());
        result.setPlayerAway(teamMatch.getAway().getMembers().getMembers().iterator().next());
        result.setPlayerHomeHandicap(result.getPlayerHome().getHandicap(teamMatch.getSeason()));
        result.setPlayerAwayHandicap(result.getPlayerAway().getHandicap(teamMatch.getSeason()));
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
