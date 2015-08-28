package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Team;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamRepository extends MongoRepository<Team,String> {
}
