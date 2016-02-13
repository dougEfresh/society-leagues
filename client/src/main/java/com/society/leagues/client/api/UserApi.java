package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.TokenReset;
import com.society.leagues.client.api.domain.User;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

import java.util.List;
import java.util.Map;

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

    @RequestLine("POST /api/user/reset/request")
    TokenReset resetRequest(User user);

    @RequestLine("POST /api/signup")
    User signupFacebook(User user);

    @RequestLine("GET /api/logout")
    String logout();

    @RequestLine("PUT /api/user/modify/profile")
    User modifyProfile(User user);

    @RequestLine("DELETE /api/user/fb/profile/{id}")
    User deleteFbProfile(@Param("id") String id);

    @RequestLine("POST /api/user/reset/password/{token}")
    User resetPassword(@Param("token") String token, Map<String,String> user);
}
