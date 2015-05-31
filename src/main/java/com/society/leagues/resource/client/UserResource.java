package com.society.leagues.resource.client;

import com.society.leagues.adapters.UserAdapter;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.dao.*;
import com.society.leagues.email.EmailSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
@SuppressWarnings("unused")
public class UserResource  {
    @Autowired UserDao dao;
    @Autowired PlayerDao playerDao;
    @Autowired PlayerResultDao playerResultDao;
    @Autowired ChallengeDao challengeDao;
    @Autowired TeamResultDao teamResultDao;
    @Autowired ChallengeResource challengeResource;
    @Autowired EmailSender emailSender;
    static List<String> resetTokens = new ArrayList<>();

    private static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @RequestMapping(value = "/user", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter get(Principal principal) {
	if (principal == null) {
	    return UserAdapter.DEFAULT_USER;
	}
        return  get(principal.getName());
    }

    @RequestMapping(value = "/user/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public UserAdapter get(@PathVariable Integer id) {
        return get().stream().filter(u->u.getUserId().equals(id)).findFirst().orElse(null);
    }

    @RequestMapping(value = "/reset/request", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Map reset(@RequestBody User user) {
        String token = UUID.randomUUID().toString().replaceAll("-","");
        logger.info("Reset Token Request: " + token);
        resetTokens.add(token);
        Map<Object,Object> tokenMap = new HashMap<>();
        tokenMap.put(token,token);
        return tokenMap;
    }

    @RequestMapping(value = "/reset/password/{token}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public Boolean reset(@PathVariable String token, @RequestBody User user) {
        User resetUser = dao.get(user.getLogin());
        if (!resetTokens.contains(token)) {
            return false;
        }
        resetUser.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        resetTokens.remove(token);
        return dao.modify(resetUser) != null;
    }

    @RequestMapping(value = "/users", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public List<UserAdapter> get() {
        List<User> users = dao.get().stream().sorted(new Comparator<User>() {
            @Override
            public int compare(User o1, User o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }).filter(u -> !(
                        u.getName().contains("FORFEIT") || u.getName().contains("BYE")  || u.getName().contains("HANDICAP")
                )
        ).collect(Collectors.toList());
        return users.stream().map(user -> new UserAdapter(user, playerDao.getByUser(user), challengeResource.getChallenges(user.getId()))).collect(Collectors.toList());
    }

    public UserAdapter get(String login) {
        User u = dao.get(login);
        return  new UserAdapter(u,playerDao.getByUser(u),challengeResource.getChallenges(u.getId()));
    }

}
