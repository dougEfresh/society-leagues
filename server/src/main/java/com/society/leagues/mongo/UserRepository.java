package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User,String> {

    User findByLogin(String login);
}
