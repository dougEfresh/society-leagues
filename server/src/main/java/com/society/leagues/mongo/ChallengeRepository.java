package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChallengeRepository extends MongoRepository<Challenge,String> {
}
