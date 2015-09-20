package com.society.leagues;

import com.mangofactory.swagger.plugin.EnableSwagger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan("com.society")
@EnableAutoConfiguration
@EnableScheduling
@EnableSwagger
public class Main implements CommandLineRunner {
    @Autowired
    ConvertUtil convertUtil;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Main.class);
        app.setWebEnvironment(true);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            if (arg.toLowerCase().contains("convert")) {
                convertUtil.convertUser();
                convertUtil.convertSeason();
                convertUtil.convertTeam();
                convertUtil.convertTeamMembers();
                convertUtil.converTeamMatch();
                convertUtil.converTeamMatchResult();
                convertUtil.convertChallengers();

                //convertUtil.convertPlayerResults();
                //convertUtil.userHandicap();
                //convertUtil.updateSetWinsLoses();


                System.exit(0);
            }
        }

    }

}

