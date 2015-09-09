package com.society.leagues.conf;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.society.leagues.CachedCollection;
import com.society.leagues.CustomRefResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.util.List;

@Configuration
@EnableConfigurationProperties(MongoProperties.class)
@SuppressWarnings("unused")
public class MongoConfig extends AbstractMongoConfiguration {

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

		//DbRefResolver dbRefResolver = new DefaultDbRefResolver(mongoDbFactory());
        CustomRefResolver customRefResolver = new CustomRefResolver(mongoDbFactory());
        customRefResolver.setCachedCollections(cachedCollections);
		MappingMongoConverter converter = new MappingMongoConverter(customRefResolver, mongoMappingContext());
		converter.setCustomConversions(customConversions());

		return converter;
	}


}
