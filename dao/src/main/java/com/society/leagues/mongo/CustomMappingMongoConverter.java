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
        /*
        if (cacheUtil == null || dbo.get("_id") == null)
            return super.read(clazz,dbo);

        try {
            Object obj = clazz.newInstance();
            if (obj instanceof LeagueObject) {
                ((LeagueObject) obj).setId(dbo.get("_id").toString());
                LeagueObject leagueObject = cacheUtil.get((LeagueObject) obj);
                if (leagueObject != null)
                    return (S) leagueObject;
            }
        } catch (InstantiationException | IllegalAccessException e) {

        }
        */
        return super.read(clazz,dbo);
    }



    public void setCacheUtil(CacheUtil cacheUtil ) {
        this.cacheUtil = cacheUtil;

    }
}
