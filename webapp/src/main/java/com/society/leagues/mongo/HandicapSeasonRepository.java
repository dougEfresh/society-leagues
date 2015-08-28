package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.HandicapSeason;
import org.springframework.data.mongodb.repository.MongoRepository;

public  interface HandicapSeasonRepository extends MongoRepository<HandicapSeason,String> {
}
