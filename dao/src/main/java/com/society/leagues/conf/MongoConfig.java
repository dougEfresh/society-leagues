package com.society.leagues.conf;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.mongo.CustomRefResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DbRefResolver;
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
@SuppressWarnings("unused")
@Profile("!test")
public class MongoConfig extends AbstractMongoConfiguration {
    @Value("${use-cache}")
    boolean useCache;

    @Autowired MongoProperties properties;
    @Autowired CacheUtil cacheUtil;

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
            dbRefResolver = new CustomRefResolver(mongoDbFactory(),cacheUtil);
        }

		MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
		converter.setCustomConversions(customConversions());

		return converter;
	}


}
