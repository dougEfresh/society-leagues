package com.society.leagues;

import com.mangofactory.swagger.plugin.EnableSwagger;
import com.society.leagues.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.social.SocialWebAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.util.List;

@Configuration
@ComponentScan("com.society")
@EnableAutoConfiguration
@EnableScheduling
@EnableSwagger
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Main implements CommandLineRunner {
    @Autowired ConvertUtil convertUtil;
    @Autowired List<MongoRepository> mongoRepositories;
    @Autowired StatService statService;

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(Main.class);
        app.setWebEnvironment(true);
        app.run(args);
    }

    @Override
    public void run(String... args) throws Exception {
        for (String arg : args) {
            if (arg.toLowerCase().contains("convert")) {
                statService.setEnableRefresh(false);
                for (MongoRepository mongoRepository : mongoRepositories) {
                    mongoRepository.deleteAll();
                }
                convertUtil.convertUser();
                convertUtil.convertSeason();
                convertUtil.convertTeam();
                convertUtil.convertTeamMembers();
                convertUtil.converTeamMatch();
                convertUtil.converTeamMatchResult();
                convertUtil.convertPlayerResults();
                convertUtil.convertChallengers();
                convertUtil.userHandicap();
                convertUtil.teamMembers();
                convertUtil.convertScramble();
                convertUtil.scrambleResults();
                convertUtil.scrambleMembers();
                convertUtil.scrambleClean();
                convertUtil.captains();
                convertUtil.clean();
                convertUtil.stats();

                //convertUtil.updateSetWinsLoses();
                System.exit(0);
            }
        }

    }

}

