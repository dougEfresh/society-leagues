package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChallengRepository extends MongoRepository<Challenge,String> {

    Challenge findByChallenger(User u);
    Challenge findByOpponent(User u);
}
