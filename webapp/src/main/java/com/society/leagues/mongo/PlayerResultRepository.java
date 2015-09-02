package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface PlayerResultRepository extends MongoRepository<PlayerResult,String> {

    @Query
    List<PlayerResult> findBySeason(Season season);

    @Query
    List<PlayerResult> findByPlayerHomeOrPlayerAway(User user);
}
