package com.society.leagues.resource;

import com.society.leagues.client.api.admin.SeasonAdminApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.dao.SeasonAdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"ADMIN"})
public class SeasonAdminResource extends AdminApiResource implements SeasonAdminApi {
    private static Logger logger = LoggerFactory.getLogger(SeasonAdminResource.class);

    @Autowired SeasonAdminDao dao;
    @Override
    public Season create(Season season) {
        if (season == null ||
                season.getDivision() == null ||
                season.getDivision().getId() == null ||
                season.getName() == null ||
                season.getStartDate() == null) {
            logger.error("Invalid Season: " + season);
            return null;
        }
        return dao.create(season);
    }

    @Override
    public Boolean delete(Season season) {
        if (season == null || season.getId() == null) {
            logger.error("Invalid Season: " + season);
            return null;
        }
        return dao.delete(season);
    }

    @Override
    public Season modify(Season season) {
        if (season ==  null || season.getId() == null || season.getName() == null) {
            logger.error("Invalid Season: " + season);
            return null;
        }
        return dao.modify(season);
    }
}
