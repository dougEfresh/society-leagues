package com.society.leagues.test;

import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
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

import java.io.IOException;

@Configuration
public class MongoConfig {
    @Autowired MongoProperties properties;
    @Autowired(required = false)
    MongoClientOptions options;
    private static final MongodStarter starter = MongodStarter.getDefaultInstance();

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
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
    }
}
