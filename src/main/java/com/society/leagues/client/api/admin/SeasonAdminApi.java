package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.Season;


public interface SeasonAdminApi {

    Season create(final Season season);

    Boolean delete(final Season season);

    Season modify(Season season);
}
