package com.society.leagues.resource;

import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.UserService;
import com.society.leagues.client.api.domain.TokenReset;
import com.society.leagues.client.api.domain.User;
import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@Api(basePath = "/api/user",value = "User" ,description = "User API", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/api/user")
@SuppressWarnings("unused")
public class UserResource {

    static Logger logger = LoggerFactory.getLogger(UserResource.class);
    @Autowired UserService userService;
    @Autowired LeagueService leagueService;
    @Autowired ChallengeService challengeService;

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


    @RequestMapping(value = "/active", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> active(Principal principal) {
        User u = get(principal.getName());
        if (u.isAdmin()) {
            return leagueService.findAll(User.class).stream().filter(user->user.isActive())
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
        user = leagueService.save(user);
        if (user.isChallenge()) {
            challengeService.createChallengeUser(user);
        }

        return user;
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

        user = leagueService.save(user);
        if (user.isChallenge()) {
            challengeService.createChallengeUser(user);
        }
        return user;
    }

    static final Comparator<User> sortUsers = new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase());
            }
        };

    @RequestMapping(value = "/season/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> listByUser(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        if (u.isAdmin())
            return leagueService.findAll(User.class).stream().sorted(sortUsers).collect(Collectors.toList());

        return leagueService.findAll(User.class).stream()
                .filter(user -> user.hasSameSeason(u)).
                filter(user->!user.isFake()).filter(user->user.isActive()).
                sorted(sortUsers)
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
        //user = leagueService.findOne(user);
        TokenReset reset = userService.resetRequest(u);
        return u.isAdmin() ? reset : new TokenReset("");
    }

    @RequestMapping(value = "/reset/password/{token}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public User reset(@PathVariable String token, @RequestBody User user) {
        User existingUser = leagueService.findByLogin(user.getLogin());
        logger.info("Got reset password request for " + token + " " + existingUser.getLogin());
        if (!existingUser.getTokens().contains(new TokenReset(token))) {
            return User.defaultUser();
        }
        existingUser.getTokens().clear();
        existingUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return leagueService.save(existingUser);
    }


}
