package com.society.leagues.resource;

import com.society.leagues.client.admin.api.TeamAdminApi;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.dao.TeamAdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"Root","Operator"})
@SuppressWarnings("unused")
public class TeamAdminResource extends AdminApiResource implements TeamAdminApi {
    private static Logger logger = LoggerFactory.getLogger(TeamAdminResource.class);
    @Autowired TeamAdminDao dao;

    @Override
    public Team create(Team team) {
        if (team == null || team.getName() == null) {
            logger.error("Could not verify team: " + team);
            return null;
        }

        return dao.create(team);
    }

    @Override
    public Boolean delete(Team team) {
        if (team == null || team.getId() == null) {
            logger.error("Could not verify team: " + team);
            return Boolean.FALSE;
        }
        return dao.delete(team);
    }
}
