package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.User;


public interface UserAdminApi {

    User create(User user);

    Boolean delete(User user);

    User modify(User user);
}
