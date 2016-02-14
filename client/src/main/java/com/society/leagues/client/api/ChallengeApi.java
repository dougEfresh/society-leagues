package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.client.api.domain.Team;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface ChallengeApi {

    @RequestLine("GET /api/challenge/user/{userId}")
    List<Challenge> challengesForUser(@Param("userId") String userId);

    @RequestLine("DELETE /api/challenge/cancel")
    Challenge cancel(Challenge challenge);

    @RequestLine("POST /api/challenge/accept")
    Challenge accept(Challenge challenge);

    @RequestLine("GET /api/challenge/date/{date}")
    @Deprecated
    List<Team> challengeUsersOnDate(@Param("date") String date);

    @RequestLine("GET /api/challenge/slots")
    List<Slot> challengeSlots();

    @RequestLine("GET /api/challenge/slots/{date}/{userId}")
    List<Slot> getAvailableSlotsForUsers(@Param("userId") String userId, @Param("date") String date);

    @RequestLine("POST /api/challenge/create")
    Challenge challenge(Challenge challenge);

    @RequestLine("GET /api/challenge/get")
    List<Challenge> challenges();

}
