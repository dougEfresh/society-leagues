package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.TeamMatch;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamMatchRepository extends MongoRepository<TeamMatch,String> {
}
