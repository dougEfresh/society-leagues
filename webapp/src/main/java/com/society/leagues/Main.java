package com.society.leagues;

import com.society.leagues.resource.ConvertResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.web.WebMvcAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("com.society.leagues")
@EnableAutoConfiguration
@EnableScheduling
public class Main implements CommandLineRunner {
    @Autowired ConvertResource convertResource;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Main.class);
        app.setWebEnvironment(true);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            if (arg.toLowerCase().contains("convert")) {
                //convertResource.convertUser();
                //convertResource.convertSeason();
                //convertResource.convertTeam();
//                convertResource.converTeamMatch();
//                convertResource.converTeamMatchResult();
                //convertResource.convertPlayerResults();
                convertResource.userHandicap();
//                convertResource.findUserSeasons();
                System.exit(0);
            }
        }

    }

}

