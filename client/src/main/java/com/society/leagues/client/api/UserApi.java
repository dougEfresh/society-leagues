package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.User;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface UserApi {

    @RequestLine("GET /api/user")
    User get();

    @RequestLine("GET /api/user/active")
    List<User> active();

    @RequestLine("GET /api/user/{id}")
    User get(@Param("id") String id);

    @RequestLine("GET /api/user/admin/modify")
    User modify(User user);

    @RequestLine("GET /api/user/admin/create")
    User create(User user);
}
