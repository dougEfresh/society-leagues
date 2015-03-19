package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.TeamResult;

public interface TeamResultAdminApi {

    TeamResult create(TeamResult teamResult);

    TeamResult modify(TeamResult teamResult);

}
