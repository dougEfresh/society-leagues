package com.society.leagues.conf;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.society.leagues.CachedCollection;
import com.society.leagues.CustomRefResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.List;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
@SuppressWarnings("unused")
public class MongoConfig extends AbstractMongoConfiguration {
    @Value("${use-cache}")
    boolean useCache;

    @Autowired MongoProperties properties;
    @Autowired List<CachedCollection> cachedCollections;

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }

    @Override
    public Mongo mongo() throws Exception {
        return new MongoClient(properties.getHost());
    }

    @Bean
    @Primary
    @Override
	public MappingMongoConverter mappingMongoConverter() throws Exception {
        DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        if (useCache) {
            dbRefResolver = new CustomRefResolver(mongoDbFactory());
            ((CustomRefResolver) dbRefResolver).setCachedCollections(cachedCollections);
        }

		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
		converter.setCustomConversions(customConversions());

		return converter;
	}


}
