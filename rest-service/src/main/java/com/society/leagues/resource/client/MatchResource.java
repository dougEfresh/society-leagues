package com.society.leagues.resource.client;

import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.dao.MatchDao;
import com.society.leagues.resource.ApiResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@SuppressWarnings("unused")
public class MatchResource extends ApiResource implements MatchApi {
    @Autowired MatchDao dao;

    @Override
    public TeamMatch get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<TeamMatch> get() {
        return dao.get();
    }

    @Override
    public List<TeamMatch> get(List<Integer> id) {
        return dao.get(id);
    }

    @Override
    public List<TeamMatch> getByTeam(Team team) {
        return dao.getByTeam(team);
    }
}

