package com.society.leagues.resource;

import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.User;
import com.wordnik.swagger.annotations.Api;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Api(basePath = "/api/user",value = "User" ,description = "User API", produces = MediaType.APPLICATION_JSON_VALUE)
@RequestMapping(value = "/api/user")
@SuppressWarnings("unused")
public class UserResource {

    @Autowired LeagueService leagueService;
    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

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
        return leagueService.save(user);
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public User modify(@RequestBody User user) {
        User existingUser = leagueService.findByLogin(user.getLogin());
        if (existingUser == null) {
            return User.defaultUser();
        }
        return leagueService.save(user);
    }

    @RequestMapping(value = "/season/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<User> listByUser(@PathVariable String id) {
        User u = leagueService.findOne(new User(id));
        return  leagueService.findAll(User.class).stream().filter(user -> user.hasSameSeason(u)).
                sorted(new Comparator<User>() {
                    @Override
                    public int compare(User user, User t1) {
                        return user.getName().compareTo(t1.getName());
                    }
                }).
                collect(Collectors.toList());
    }

}
