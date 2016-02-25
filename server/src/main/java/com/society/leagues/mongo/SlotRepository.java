package com.society.leagues.mongo;

import com.society.leagues.client.api.domain.Slot;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SlotRepository extends MongoRepository<Slot,String> {
}
