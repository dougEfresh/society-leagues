package com.society.leagues.resource;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.HandicapSeason;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.exception.InvalidRequestException;
import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/user")
@SuppressWarnings("unused")
public class UserResource {

    static Logger logger = LoggerFactory.getLogger(UserResource.class);
    @Autowired UserService userService;
    @Autowired LeagueService leagueService;
    @Autowired ChallengeService challengeService;

    @RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
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
            return leagueService.findAll(User.class).stream().filter(User::isActive)
                    .sorted((user, t1) -> user.getName().compareTo(t1.getName())).collect(Collectors.toList());
        }

        return listByUser(u.getId());
    }

    @RequestMapping(value = "/login/{login}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(@PathVariable String login) {
        return leagueService.findByLogin(login);
    }

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User create(@RequestBody User user) {
        User oldUser = leagueService.findByLogin(user.getLogin());
        if (oldUser != null) {
            return oldUser;
        }

        user.setEmail(user.getLogin());
        user = leagueService.save(user);
        if (user.isChallenge()) {
            challengeService.createChallengeUser(user);
        }

        return user;
    }

    @RequestMapping(value = "/admin/create/challenge", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
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
    public User modify(@RequestBody User user) {
        User existingUser = leagueService.findOne(user);
        if (existingUser == null) {
            existingUser = new User();
        }
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setLogin(user.getLogin());
        existingUser.setEmail(user.getLogin());
        existingUser.setStatus(user.getStatus());
        existingUser.setRole(user.getRole());
        logger.info("Modify user " + user.getName());

        if (user.getHandicapSeasons() != null && !user.getHandicapSeasons().isEmpty()) {
            for (HandicapSeason handicapSeason : user.getHandicapSeasons().stream().filter(s->s.getSeason().isActive()).collect(Collectors.toList())) {
                HandicapSeason newHandicapSeason =
                        new HandicapSeason(handicapSeason.getHandicap(),
                                leagueService.findOne(handicapSeason.getSeason()));
                    if (newHandicapSeason.getHandicap() == Handicap.NA) {
                        logger.info("Removing Handicap season " + handicapSeason.getSeason().getName());
                        existingUser.removeHandicap(newHandicapSeason);
                    } else {
                        logger.info("Adding Handicap season " + handicapSeason.getSeason().getName());
                        existingUser.removeHandicap(newHandicapSeason);
                        existingUser.addHandicap(newHandicapSeason);
                    }
            }
        }
        existingUser = leagueService.save(existingUser);
        if (existingUser.isChallenge()) {
            challengeService.createChallengeUser(existingUser);
        }
        return existingUser;
    }



    @RequestMapping(value = "/season/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> listByUser(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        if (u.isAdmin())
            return leagueService.findAll(User.class).stream().sorted(User.sort).collect(Collectors.toList());

        return leagueService.findAll(User.class).stream()
                .filter(user -> user.hasSameSeason(u)).
                filter(user->!user.isFake()).filter(User::isActive).
                sorted(User.sort)
                .collect(Collectors.toList());
    }

    @RequestMapping(value = "/modify/profile", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    public User modifyProfile(@RequestBody User user) {
        User exitsting = leagueService.findOne(new User(user.getId()));
        if (exitsting == null){
            throw new InvalidRequestException("Invalid user " + user.getId());
        }

        exitsting.setUserProfile(user.getUserProfile());
        return leagueService.save(exitsting);
    }

    @RequestMapping(value = "/profile/fb/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
    public User deleteFbProfile(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        if (u == null){
            throw new InvalidRequestException("Invalid user " + id);
        }

        u.getUserProfile().setImageUrl(null);
        u.getUserProfile().setProfileUrl(null);

        return leagueService.save(u);
    }
}