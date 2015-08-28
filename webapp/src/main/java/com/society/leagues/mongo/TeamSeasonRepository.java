package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.TeamSeason;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamSeasonRepository extends MongoRepository<TeamSeason,String> {
}
