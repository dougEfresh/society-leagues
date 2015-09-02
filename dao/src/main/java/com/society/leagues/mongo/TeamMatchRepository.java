package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface TeamMatchRepository extends MongoRepository<TeamMatch,String> {
    List<TeamMatch> findBySeason(Season season);
}
