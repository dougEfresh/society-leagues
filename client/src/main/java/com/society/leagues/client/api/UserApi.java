package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.User;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;

@Headers({"Accept: application/json, */*","Content-Type: application/json","Accept-Encoding: gzip, deflate, sdch" })
//@Headers({"Accept: application/json, */*","Content-Type: application/json"})
public interface UserApi {

    @RequestLine("GET /api/user")
    @Headers("X-Cache: false")
    User get();

    @RequestLine("GET /api/user/active")
    List<User> active();

    @RequestLine("GET /api/user/{id}")
    User get(@Param("id") String id);

    @RequestLine("POST /api/user/admin/modify")
    User modify(User user);

    @RequestLine("POST /api/user/admin/create")
    User create(User user);

    @RequestLine("GET /api/user/all")
    List<User> all();
}
