package com.society.leagues.feign;

import com.society.leagues.adapters.UserAdapter;
import feign.Param;
import feign.RequestLine;

public interface UserRestApi {

    @RequestLine("GET /api/user/{id}")
    UserAdapter getUser(@Param("id") Integer id);
}
