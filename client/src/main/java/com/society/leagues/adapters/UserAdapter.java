package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import java.util.stream.Collectors;

public class UserAdapter {

    User user;
    List<Player> players = new ArrayList<>();
    Map<Status,List<UserChallengeGroup>> challenges = new HashMap<>();
    List<PlayerResult> chalengeResults;
    public static UserAdapter DEFAULT_USER;
    public static Integer period = 10;
    static {
        User u = new User();
        u.setId(0);
        u.setFirstName("");
        u.setLastName("");
        DEFAULT_USER = new UserAdapter(u,Collections.emptyList(),Collections.emptyMap());
    }

    public UserAdapter(User user, List<Player> players, Map<Status,List<UserChallengeGroup>> challenges) {
        this.user = user;
        this.players = players;
        this.challenges = challenges;
    }

    public UserAdapter() {
    }
    
    public Integer getUserId() {
	return user.getId();
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public Role getRole() {
        return user.getRole();
    }

    public Set<Integer> getSeasons() {
        Set<Integer> ids = new HashSet<>();
        for (Player player : players) {
            ids.add(player.getSeason().getId());
        }
        return ids;
    }

    public Set<Integer> getTeams() {
        Set<Integer> teams = new HashSet<>();
        for (Player player : players.stream().filter(p-> !p.getDivision().isChallenge()).collect(Collectors.toList())) {
            teams.add(player.getTeam().getId());
        }
        return teams;
    }

    public Map<Integer,Handicap> getCurrentHandicap() {
        Map<Integer,Handicap> handicap = new HashMap<>();
        for(Integer seasonId: getSeasons()) {
            handicap.put(seasonId, players.stream().filter(p -> p.getSeason().getId().equals(seasonId)).max(new Comparator<Player>() {
                @Override
                public int compare(Player player, Player t1) {
                    //Total Hack
                    if (player.getEnd() == null) {
                        return player.getId().compareTo(t1.getId());
                    }
                    if (t1.getEnd() == null) {
                        return player.getId().compareTo(t1.getId());
                    }
                    return player.getEnd().compareTo(t1.getEnd());
                }
            }).get().getHandicap());
        }
        return handicap;
    }

    @JsonIgnore
    public boolean isChallenge() {
        Optional<Player> playerOptional =  players.stream().filter(p->p.getDivision().isChallenge()).findFirst();
        return playerOptional.isPresent();
    }

    public Map<Status,List<UserChallengeGroup>> getChallenges() {
        return challenges;
    }

    public void setChallengeResults(List<PlayerResult> results) {
        this.chalengeResults = results;
    }

    public void setChallenges(Map<Status,List<UserChallengeGroup>> challenges) {
	this.challenges = challenges;
    }

    public List<MatchPoints> getPoints() {
        List<MatchPoints> matchPoints = new ArrayList<>();
        if (chalengeResults == null) {
            return matchPoints;
        }
        chalengeResults.sort(new Comparator<PlayerResult>() {
            @Override
            public int compare(PlayerResult playerResult, PlayerResult t1) {
                return t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate());
            }
        });

        double matchNum = 0;
        double p = (double) period;
        for (PlayerResult chalengeResult : chalengeResults) {
            if (matchNum > 7) {
                break;
            }
            PlayerResultRawAdapter playerResultRawAdapter = new PlayerResultRawAdapter(chalengeResult);
            int points = 1;
            if (playerResultRawAdapter.getWinner().equals(this.getUserId())) {
                points += 2;
            } else if (playerResultRawAdapter.getWinnerRacks() - playerResultRawAdapter.getLoserRacks() == 1) {
                points += 1;
            }

            matchPoints.add(new MatchPoints(chalengeResult.getId(), points, (double) points/(p/(p-matchNum)), (int) matchNum));
            matchNum++;
        }
        return matchPoints;
    }

}
