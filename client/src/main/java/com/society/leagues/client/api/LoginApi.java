package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.User;
import feign.Body;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface LoginApi {

    @RequestLine("POST /api/authenticate")
    @Headers("Content-Type: application/x-www-form-urlencoded")
    @Body("username={username}&password={password}&springRememberMe=true")
    User login(@Param("username") String username, @Param("password") String password);
}
