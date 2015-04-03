package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.division.Division;

public interface DivisionAdminApi {

    Division create(final Division division);

    Boolean delete(final Division division);

    Division modify(Division division);
}
