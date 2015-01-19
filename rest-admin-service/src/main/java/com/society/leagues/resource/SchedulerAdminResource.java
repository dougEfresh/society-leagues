package com.society.leagues.resource;

import com.society.leagues.client.api.admin.SchedulerAdminApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.dao.SchedulerAdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Component
@RolesAllowed(value = {"ADMIN"})
@SuppressWarnings("unused")
public class SchedulerAdminResource extends AdminApiResource implements SchedulerAdminApi {
    @Autowired SchedulerAdminDao dao;

    @Override
    public List<Match> create(Integer seasonId, List<Team> teams) {
        return dao.create(seasonId,teams);
    }

    @Override
    public Match create(Match match) {
        return dao.create(match);
    }
}
