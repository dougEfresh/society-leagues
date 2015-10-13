package com.society.leagues.mongo;

import com.mongodb.DBObject;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.cache.CachedCollection;
import com.society.leagues.client.api.domain.LeagueObject;
import org.springframework.data.mapping.context.MappingContext;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.MongoPersistentEntity;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;

public class CustomMappingMongoConverter extends MappingMongoConverter {
    CacheUtil cacheUtil;

    public CustomMappingMongoConverter(DbRefResolver dbRefResolver, MappingContext<? extends MongoPersistentEntity<?>, MongoPersistentProperty> mappingContext) {
        super(dbRefResolver, mappingContext);
    }

    public <S extends Object> S read(Class<S> clazz, final DBObject dbo) {
        return super.read(clazz,dbo);
    }

    public void setCacheUtil(CacheUtil cacheUtil ) {
        this.cacheUtil = cacheUtil;

    }
}
