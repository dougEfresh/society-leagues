package com.society.leagues.test;

import com.mongodb.Mongo;
import com.mongodb.MongoClientOptions;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.mongo.CustomRefResolver;
import de.flapdoodle.embed.mongo.*;
import de.flapdoodle.embed.mongo.config.*;
import de.flapdoodle.embed.mongo.distribution.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mongo.MongoProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.Resource;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.config.EnableMongoAuditing;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Configuration
@EnableMongoAuditing
public class TestMongoConfig extends AbstractMongoConfiguration {
    @Autowired MongoProperties properties;
    @Autowired(required = false)
    MongoClientOptions options;
    static final MongodStarter starter = MongodStarter.getDefaultInstance();
    @Autowired CacheUtil cacheUtil;
    @Value("classpath:/leagues/user.json")
    Resource userImport;
    @Value("classpath:/leagues/team.json")
    Resource teamImport;
    @Value("classpath:/leagues/teamMembers.json")
    Resource teamMembersImport;
    @Value("classpath:/leagues/season.json")
    Resource seasonImport;

    @PostConstruct
    public void init() throws IOException {
        /*processImport(seasonImport,"season");
        processImport(userImport,"user");
        processImport(teamMembersImport,"teamMembers");
        processImport(teamImport,"team");
        */
    }

    private void processImport(Resource resource,String collection) throws IOException {
        IMongoImportConfig importProcess = new MongoImportConfigBuilder()
                .db(getDatabaseName())
                .version(Version.Main.PRODUCTION)
                .net(new Net(mongod().getConfig().net().getPort(),mongod().getConfig().net().isIpv6()))
                .collection(collection)
                .dropCollection(true)
                .jsonArray(true)
                .upsert(true)
                .importFile(resource.getFile().getAbsolutePath()).build();
        MongoImportExecutable mongoImportExecutable = MongoImportStarter.getDefaultInstance().prepare(importProcess);
        MongoImportProcess mongoImport = mongoImportExecutable.start();
    }

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
        CustomRefResolver dbRefResolver = new CustomRefResolver(mongoDbFactory(),cacheUtil);
        MappingMongoConverter converter = new MappingMongoConverter(dbRefResolver, mongoMappingContext());
        converter.setCustomConversions(customConversions());
        return converter;
    }

    @Bean
    public IMongodConfig mongodConfig() throws IOException {
        return new MongodConfigBuilder().version(Version.Main.PRODUCTION).build();
    }

}
