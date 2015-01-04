package com.society.leagues.resource;

import com.society.leagues.client.admin.api.LeagueAdminApi;
import com.society.leagues.client.api.domain.league.League;
import com.society.leagues.dao.LeagueAdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"Root","Operator"})
@SuppressWarnings("unused")
public class LeagueResource extends AdminApiResource implements LeagueAdminApi  {

    @Autowired
    LeagueAdminDao dao;
    private static Logger logger = LoggerFactory.getLogger(LeagueResource.class);

    @Override
    public League create(League league) {
        if (league.getDues() == null || league.getType() == null) {
            logger.error("League is not verified: "+ league);
            return null;
        }
        return dao.create(league);
    }

    @Override
    public Boolean delete(Integer id) {
        return dao.delete(id);
    }

    @Override
    public League modify(League league) {
        if (league.getDues() == null || league.getType() == null || league.getId() == null) {
            logger.error("League is not verified: "+ league);
            return null;
        }
        return dao.modify(league);
    }
}
