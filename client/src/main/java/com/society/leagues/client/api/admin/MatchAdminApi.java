package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.TeamMatch;

public interface MatchAdminApi {

    TeamMatch create(TeamMatch teamMatch);

    TeamMatch modify(TeamMatch teamMatch);
    
}
