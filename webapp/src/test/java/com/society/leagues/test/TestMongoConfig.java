package com.society.leagues.test;

import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import com.society.leagues.cache.CachedCollection;
import com.society.leagues.mongo.CustomRefResolver;
import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodProcess;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import java.io.IOException;
import java.util.List;

@Configuration
@EnableMongoAuditing
public class TestMongoConfig  extends AbstractMongoConfiguration {
    @Autowired MongoProperties properties;
    @Autowired(required = false)
    MongoClientOptions options;
    static final MongodStarter starter = MongodStarter.getDefaultInstance();
    @Autowired List<CachedCollection> cachedCollections;

    @Override
    protected String getDatabaseName() {
        return properties.getDatabase();
    }

    @Bean(destroyMethod = "close")
    public Mongo mongo() throws IOException {
        Net net = mongod().getConfig().net();
        properties.setHost(net.getServerAddress().getHostName());
        properties.setPort(net.getPort());
        return properties.createMongoClient(this.options);
    }

    @Bean(destroyMethod = "stop")
    public MongodProcess mongod() throws IOException {
        return mongodExe().start();
    }

    @Bean(destroyMethod = "stop")
    public MongodExecutable mongodExe() throws IOException {
        return starter.prepare(mongodConfig());
    }

    @Bean
    @Primary
    @Override
    public MappingMongoConverter mappingMongoConverter() throws Exception {
        CustomRefResolver dbRefResolver = new CustomRefResolver(mongoDbFactory(),cachedCollections);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        return converter;
    }

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
    }
}
