package com.society.leagues;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.mongo.UserRepository;
import com.society.leagues.service.StatService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.List;

@Configuration
@ComponentScan("com.society")
@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
@EnableScheduling
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class Main implements CommandLineRunner {
    private static Logger logger = Logger.getLogger(ConvertUtil.class);

    @Autowired ConvertUtil convertUtil;
    @Autowired List<MongoRepository> mongoRepositories;
    @Autowired StatService statService;
    @Autowired Environment environment;
    @Autowired UserRepository userRepository;
    String def = new BCryptPasswordEncoder().encode("abd123");

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
                //convertUtil.updateRacks();
                convertUtil.convertUser();
                //convertUtil.updateSetWinsLoses();
                System.exit(0);
            }
        }

        if (environment.acceptsProfiles("dev")) {
            List<User> users = userRepository.findAll();
            for (User user : users) {
                user.setPassword(def);
            }
            logger.info("Resetting password");
            userRepository.save(users);
        }
    }
}

