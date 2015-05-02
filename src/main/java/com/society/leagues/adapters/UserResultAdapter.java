package com.society.leagues.adapters;

import com.society.leagues.client.api.domain.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserResultAdapter {

    List<PlayerResultAdapter> results;
    User user;

    public UserResultAdapter(User user, List<PlayerResultAdapter> results) {
        this.results = results;
        this.user = user;
    }

    public Map<Integer,List<PlayerResultAdapter>> getResults() {
        HashMap<Integer,List<PlayerResultAdapter>> teamMatchResults = new HashMap<>();
        for (PlayerResultAdapter result : results) {
            if (!teamMatchResults.containsKey(result.teamMatch().getId())) {
                teamMatchResults.put(result.teamMatch().getId(),new ArrayList<>());
            }
            teamMatchResults.get(result.teamMatch().getId()).add(result);
        }
        return teamMatchResults;
    }
}
