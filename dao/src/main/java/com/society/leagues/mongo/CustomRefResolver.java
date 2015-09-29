package com.society.leagues.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.DBRef;
import com.society.leagues.cache.CacheUtil;
import org.apache.log4j.Logger;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.convert.DbRefProxyHandler;
import org.springframework.data.mongodb.core.convert.DbRefResolverCallback;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.mapping.MongoPersistentProperty;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;


public class CustomRefResolver extends DefaultDbRefResolver {
    private static Logger logger = Logger.getLogger(CustomRefResolver.class);
    final CacheUtil cacheUtil;
    static long lookups = 1;
    static long cacheHits = 1;

    public CustomRefResolver(MongoDbFactory mongoDbFactory, final CacheUtil cacheUtil) {
        super(mongoDbFactory);
        this.cacheUtil = cacheUtil;
    }

    @Override
    public Object resolveDbRef(MongoPersistentProperty property, DBRef dbref, DbRefResolverCallback callback, DbRefProxyHandler handler) {
        if (dbref == null || dbref.getCollectionName() == null || dbref.getId() == null || dbref.getId().toString() == null) {
            return super.resolveDbRef(property, dbref, callback, handler);
        }

        Object obj = cacheUtil.findOne(dbref.getId().toString(), dbref.getCollectionName());
        if (obj == null) {
            return cacheUtil.addCache(super.resolveDbRef(property, dbref, callback, handler));
        } else {
            cacheHits++;
            return obj;
        }
    }

    @Override
    public DBObject fetch(DBRef dbRef) {
        if (dbRef == null) return null;
        if (dbRef.getCollectionName() == null) return super.fetch(dbRef);

        lookups++;
        if (lookups % 100 == 0) {
            logger.info(String.format("Cache Stats %s %s : Lookups: %d  CacheHits: %d Ratios: %f ",
                    dbRef.getCollectionName(), dbRef, lookups, cacheHits,
                    ((double) lookups / (double) cacheHits)));
        }
        return super.fetch(dbRef);
    }

}
