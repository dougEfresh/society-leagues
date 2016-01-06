package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Team;
import feign.RequestLine;

import java.util.List;

public interface ChallengeApi {

    @RequestLine("GET /api/challenge/users")
    List<Team> challengeUsers();
}
