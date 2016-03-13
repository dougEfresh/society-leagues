package com.society.leagues;

import com.society.leagues.client.api.domain.MatchPoints;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MatchPointService {
    private final static int period = 10;

    public List<PlayerResult> calcPoints(User user, List<PlayerResult> playerResults) {
        LocalDateTime tenWeeks = LocalDateTime.now().plusDays(1).minusWeeks(10);
        playerResults = playerResults.stream()
                .filter(r->r.getTeamMatch() != null)
                .filter(r->r.getSeason() != null)
                .filter(r->r.getSeason().isActive())
                .filter(pr -> pr.getMatchDate() != null)
                .filter(PlayerResult::hasResults)
                .filter(t->t.getMatchDate().isAfter(tenWeeks))
                .sorted((o1, o2) -> o2.getTeamMatch().getMatchDate().compareTo(o1.getTeamMatch().getMatchDate()))
                .limit(7)
                .collect(Collectors.toList());

        double matchNum = 0;
        for (PlayerResult challengeResult : playerResults) {
            MatchPoints mp = new MatchPoints();
            int points = 1;
            String[] r = challengeResult.getRace().split("/");
            int hcGames = 0;
            if (r.length == 0) {
                hcGames = 0;
            } else {
                try {
                    hcGames = Integer.parseInt(r[0]);
                } catch (NumberFormatException ignore) {}
            }
            if (challengeResult.isWinner(user)) {
                if (challengeResult.getLoserRacks() == 0) {
                    points += 1;
                } else {
                    if (challengeResult.getLoserHandicap().ordinal() < challengeResult.getWinnerHandicap().ordinal()) {
                        if (challengeResult.getLoserRacks() - hcGames <= 0)
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
            mp.setWeightedAvg((double) points / (period / (period - matchNum)));
            mp.setCalculation(String.format("(%s * (10-%s))/10", points, new Double(matchNum).intValue()));
            challengeResult.setMatchPoints(mp);
            matchNum++;
        }
        return playerResults;
    }
}
