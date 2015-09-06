package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PlayerResultRepository extends MongoRepository<PlayerResult,String> {

    List<PlayerResult> findBySeason(Season season);
    List<PlayerResult> findByPlayerHomeOrPlayerAway(User user);
    List<PlayerResult> findByPlayerHome(User user);
    List<PlayerResult> findByPlayerAway(User user);
}
