package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Season;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SeasonRepository extends MongoRepository<Season,String> {

    Season findByLegacyId(Integer id);

}
