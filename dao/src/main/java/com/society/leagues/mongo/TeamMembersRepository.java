package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.TeamMembers;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TeamMembersRepository extends MongoRepository<TeamMembers, String> {
}
