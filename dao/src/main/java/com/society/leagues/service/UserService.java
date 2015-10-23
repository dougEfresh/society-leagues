package com.society.leagues.service;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.TokenReset;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.UserProfile;
import com.society.leagues.listener.DaoListener;
import com.society.leagues.mongo.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.Period;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Component
public class UserService {
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired UserRepository userRepository;
    @Autowired EmailService emailService;
    @Autowired LeagueService leagueService;
    @Value("${service-url:http://leaguesdev.societybilliards.com}") String serviceUrl;
    @Autowired ThreadPoolTaskExecutor threadPoolTaskExecutor;
    final static Logger logger = LoggerFactory.getLogger(UserService.class);

    @PostConstruct
    public void init() {
        threadPoolTaskExecutor.setCorePoolSize(1);
        leagueService.addListener(
        new DaoListener(){
            @Override
            public void onAdd(LeagueObject object) {}

            @Override
            public void onChange(LeagueObject object) {
                if (object instanceof User) {
                    refresh();
                }
            }
            @Override
            public void onDelete(LeagueObject object) {}
        });
        refresh();
    }

    public TokenReset resetRequest(User u) {
        TokenReset reset = new TokenReset(UUID.randomUUID().toString().replaceAll("-",""));
        logger.info("Reset Token Request: " + reset.getToken() + " for " + u.getLogin());
        u.getTokens().add(reset);
        leagueService.save(u);
        try {
            emailService.email(u.getEmail(), "Password Reset Request",
                    String.format("Hello %s,\n     Please click: %s%s=%s \n to reset your password.",
                            u.getFirstName(),
                            serviceUrl,
                            "/#/reset?token",
                            reset.getToken())
            );
        } catch (Throwable t) {
            logger.error("Error communicating to email provider");
        }
        return reset;
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*10)
    public void clearTokens() {
        logger.info("Removing old tokens");
        List<User> user = leagueService.findAll(User.class).stream().parallel().filter(u->!u.getTokens().isEmpty()).collect(Collectors.toList());
        LocalDate now = LocalDate.now();
        for (User u : user) {
            Iterator<TokenReset> iterator = u.getTokens().iterator();
            while(iterator.hasNext()) {
                TokenReset reset = iterator.next();
                Period p = Period.between(reset.getCreated().toLocalDate(),now);
                if (p.getDays() > 3) {
                    logger.info("Removing " + reset);
                    iterator.remove();
                }
            }
        }
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*11)
    public void refresh() {
        logger.info("Refresh user profiles");
        threadPoolTaskExecutor.submit(
                new Runnable() {
                    @Override
                    public void run() {
                        List < Map < String, Object >> results = jdbcTemplate.queryForList("select * from UserConnection where userId is not null");
                        logger.info("Got back " + results.size());
                        for (Map<String, Object> result : results) {
                            UserProfile profile = new UserProfile();
                            profile.setProfileUrl(result.get("profileUrl").toString());
                            profile.setImageUrl(result.get("imageUrl").toString());
                            User u = leagueService.findByLogin(result.get("userId").toString());
                            if (u == null)
                                continue;
                            logger.info("Updating user profile " + u.getName()  + "  " + profile);
                            u.setUserProfile(profile);
                            userRepository.save(u);
                        }
                    }
                }
        );
    }
}
