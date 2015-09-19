package com.society.leagues.resource;

import com.society.leagues.Service.ChallengeService;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.TokenReset;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.email.EmailSender;
import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(basePath = "/api/user",value = "User" ,description = "User API", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/api/user")
@SuppressWarnings("unused")
public class UserResource {

    static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @Autowired LeagueService leagueService;
    @Autowired ChallengeService challengeService;
    @Autowired EmailSender emailSender;
    @Value("${service-url:http://leaguesdev.societybilliards.com}") String serviceUrl;


    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(Principal principal, HttpServletRequest request) {
        if (principal == null) {
            return User.defaultUser();
        }
        return  get(principal.getName());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public User getById(@PathVariable String id) {
        return leagueService.findOne(new User(id));
    }

    @RequestMapping(value = "/all", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> all(Principal principal) {
        User u = get(principal.getName());
        if (u.isAdmin()) {
            return leagueService.findAll(User.class).stream()
                    .sorted((user, t1) -> user.getName().compareTo(t1.getName())).collect(Collectors.toList());
        }

        return listByUser(u.getId());
    }

    @RequestMapping(value = "/login/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_USER')")
    public User get(@PathVariable String login) {
        return leagueService.findByLogin(login);
    }

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User create(@RequestBody User user) {
        User oldUser = leagueService.findByLogin(user.getLogin());
        if (oldUser != null) {
            return oldUser;
        }
        user.setLogin(user.getEmail());
        return leagueService.save(user);
    }

    @RequestMapping(value = "/admin/create/challenge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User createChallenge(@RequestBody final User user) {
        User oldUser = leagueService.findByLogin(user.getLogin());
        if (oldUser != null) {
            return oldUser;
        }
        user.setLogin(user.getEmail());
        leagueService.save(user);
        challengeService.createChallengeUser(user);
        return user;
    }


    @RequestMapping(value = "/admin/modify",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User modify(@RequestBody User user) {
        User existingUser = leagueService.findOne(user);
        if (existingUser == null) {
            return User.defaultUser();
        }
        user.setPassword(existingUser.getPassword());
        user.setLogin(existingUser.getLogin());
        return leagueService.save(user);
    }

    @RequestMapping(value = "/season/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> listByUser(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        return leagueService.findAll(User.class).stream().filter(user -> user.hasSameSeason(u)).
                filter(user->!user.isFake()).
                sorted(new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                return user.getName().compareTo(t1.getName());
            }
                })
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/reset/request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public TokenReset reset(Principal principal, @RequestBody User user) {
        User u = get(principal.getName());
        if (u == null) {
            return null;
        }
        if (!u.isAdmin() && !user.equals(u)) {
            logger.error("ERROR ERROR ERROR");
            return null;
        }
        TokenReset reset = new TokenReset(UUID.randomUUID().toString().replaceAll("-",""));
        logger.info("Reset Token Request: " + reset.getToken()  +" from " + user.getLogin());

        emailSender.email(u.getEmail(),"Password Reset Request",
                String.format("Hello %s,\n     Please click: %s%s=%s \n to reset your password.",
                        u.getFirstName(),
                        serviceUrl,
                        "/#/reset?token",
                        reset.getToken())
        );
        return u.isAdmin() ? reset : null;
    }

    @RequestMapping(value = "/reset/password/{token}/{id}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public User reset(@PathVariable String token, @PathVariable String id ,@RequestBody String password) {
        User existingUser = leagueService.findOne(new User(id));
        logger.info("Got reset password request for " + token + " " + existingUser.getLogin());
        if (!existingUser.getTokens().contains(new TokenReset(token))) {
            return User.defaultUser();
        }
        existingUser.getTokens().clear();
        existingUser.setPassword(new BCryptPasswordEncoder().encode(password));
        return leagueService.save(existingUser);
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*10)
    private void  clearTokens() {
        logger.info("Removing old tokens");
        List<User> user = leagueService.findAll(User.class).stream().parallel().filter(u->!u.getTokens().isEmpty()).collect(Collectors.toList());
        LocalDate now = LocalDate.now();
        for (User u : user) {
            Iterator<TokenReset> iterator = u.getTokens().iterator();
            while(iterator.hasNext()) {
                TokenReset reset = iterator.next();
                Period p = Period.between(reset.getCreated().toLocalDate(),now);
                if ( p.getDays() > 3) {
                    logger.info("Removing " + reset);
                    iterator.remove();
                }
            }
        }
    }

}
