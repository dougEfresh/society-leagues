package com.society.leagues.resource;

import com.society.leagues.client.api.admin.MatchAdminApi;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.dao.MatchDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"ADMIN"})
@SuppressWarnings("unused")
public class MatchAdminResource extends AdminApiResource implements MatchAdminApi {
    @Autowired MatchDao dao;

    @Override
    public TeamMatch create(TeamMatch teamMatch) {
        return dao.create(teamMatch);
    }

    @Override
    public TeamMatch modify(TeamMatch teamMatch) {
        return dao.modify(teamMatch);
    }
}
