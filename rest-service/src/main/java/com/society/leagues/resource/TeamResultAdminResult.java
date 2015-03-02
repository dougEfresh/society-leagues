package com.society.leagues.resource;

import com.society.leagues.client.api.admin.TeamResultAdminApi;
import com.society.leagues.client.api.domain.TeamResult;
import com.society.leagues.dao.TeamResultDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SuppressWarnings("unused")
public class TeamResultAdminResult implements TeamResultAdminApi {

    @Autowired TeamResultDao dao;

    @Override
    public TeamResult create(@RequestBody TeamResult teamResult) {
        return dao.create(teamResult);
    }

    @Override
    public TeamResult modify(@RequestBody TeamResult teamResult) {
        return dao.create(teamResult);
    }
}
