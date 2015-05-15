package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.*;
import java.util.stream.Collectors;

public class UserAdapter {

    User user;
    List<Player> players = new ArrayList<>();
    Map<Status,List<UserChallengeGroup>> challenges = new HashMap<>();
    public static UserAdapter DEFAULT_USER;

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

    @JsonIgnore
    public boolean isChallenge() {
        Optional<Player> playerOptional =  players.stream().filter(p->p.getDivision().isChallenge()).findFirst();
        return playerOptional.isPresent();
    }

    public Map<Status,List<UserChallengeGroup>> getChallenges() {
        return challenges;
    }

    public void setChallenges(Map<Status,List<UserChallengeGroup>> challenges) {
	this.challenges = challenges;
    }

}
