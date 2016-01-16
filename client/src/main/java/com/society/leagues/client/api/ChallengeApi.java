package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.client.api.domain.Team;
import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface ChallengeApi {

    @RequestLine("GET /api/challenge/users")
    List<Team> challengeUsers();

    @RequestLine("GET /api/challenge/date/{date}")
    List<Team> challengeUserOnDate(@Param("date") String date);

    @RequestLine("GET /api/challenge/slots")
    List<Slot> challengeSlots();

    @RequestLine("GET /api/challenge/slots/{date}")
    List<Slot> getAvailableSlotsOnDate(@Param("date") String date);

    @RequestLine("GET /api/challenge/slots/{date}/{userId}")
    List<Slot> getAvailableSlotsForUsers(@Param("userId") String userId, @Param("date") String date);

    @RequestLine("POST /api/challenge/create")
    Challenge challenge(Challenge challenge);

}
