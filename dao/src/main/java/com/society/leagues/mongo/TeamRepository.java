package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TeamRepository extends MongoRepository<Team,String> {
    List<Team> findBySeason(Season season);
}
