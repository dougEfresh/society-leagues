package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import feign.Headers;
import feign.RequestLine;

import java.util.List;

//@Headers({"Accept: application/json, */*;","Content-Type: application/json", "Accept-Encoding: gzip, deflate, sdch"})
@Headers({"Accept: application/json, */*","Content-Type: application/json"})
public interface ChallengeApi {

    @RequestLine("GET /api/challenge/users")
    List<Team> challengeUsers();
}
