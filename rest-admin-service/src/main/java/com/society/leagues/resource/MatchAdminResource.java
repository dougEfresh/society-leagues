package com.society.leagues.resource;

import com.society.leagues.client.api.admin.MatchAdminApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.dao.admin.MatchAdminDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"ADMIN"})
@SuppressWarnings("unused")
public class MatchAdminResource extends AdminApiResource implements MatchAdminApi {
    @Autowired MatchAdminDao dao;

    @Override
    public Match create(Match match) {
        return dao.create(match);
    }

    @Override
    public Match modify(Match match) {
        return dao.modify(match);
    }
}
