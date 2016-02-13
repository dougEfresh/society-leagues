package com.society.leagues.resource;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.HandicapSeason;
import com.society.leagues.exception.InvalidRequestException;
import com.society.leagues.exception.UnauthorizedException;
import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.UserService;
import com.society.leagues.client.api.domain.TokenReset;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/user")
@SuppressWarnings("unused")
public class UserResource {

    static Logger logger = LoggerFactory.getLogger(UserResource.class);
    @Autowired UserService userService;
    @Autowired LeagueService leagueService;
    @Autowired ChallengeService challengeService;
    @Autowired UsersConnectionRepository usersConnectionRepository;
    @Autowired ConnectionRepository connectionRepository;

    @RequestMapping(value = "", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public User get(Principal principal, HttpServletRequest request) {
        if (principal == null) {
            throw new UnauthorizedException("Unknown user");
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
            return leagueService.findAll(User.class).stream().filter(User::isActive)
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

        user.setEmail(user.getLogin());
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
         User u = leagueService.findAll(User.class).parallelStream()
                .filter(us-> us.getLogin() != null)
                .filter(us -> us.getLogin().toLowerCase().trim().equals(user.getLogin().trim())
                ).findFirst().orElse(null);
        if (u == null) {
            logger.error("Could not find user " + user.getLogin());
            return new TokenReset("");
        }
        TokenReset reset = userService.resetRequest(u);
        return u.isAdmin() ? reset : new TokenReset("");
    }

    @RequestMapping(value = "/reset/password/{token}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public User reset(@PathVariable String token, @RequestBody Map<String,String> user) {
         User existingUser = leagueService.findAll(User.class).parallelStream()
                .filter(us-> us.getLogin() != null)
                .filter(us -> us.getLogin().toLowerCase().equals(user.get("login"))
                ).findFirst().orElse(null);
        if (existingUser == null) {
            logger.error("No User Found");
            return User.defaultUser();
        }
        logger.info("Got reset password request for " + token + " " + existingUser.getEmail());
        if (existingUser.getTokens() == null)  {
            logger.error("No tokens with user " + existingUser.getEmail());
            return User.defaultUser();
        }
        for (TokenReset reset : existingUser.getTokens()) {
             if (token.equals(reset.getToken())) {
                 existingUser.getTokens().clear();
                 existingUser.setPassword(new BCryptPasswordEncoder().encode(user.get("password")));
                 return leagueService.save(existingUser);
             }
        }
        return User.defaultUser();
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
