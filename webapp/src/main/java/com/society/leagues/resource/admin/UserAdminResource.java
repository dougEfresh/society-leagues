package com.society.leagues.resource.admin;

import com.society.leagues.adapters.UserAdapter;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
import com.society.leagues.resource.client.ChallengeResource;
import com.society.leagues.resource.client.UserResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RequestMapping(value = "/api")
@RestController
public class UserAdminResource {
    @Autowired UserDao userDao;
    @Autowired UserResource userResource;
    @Autowired ChallengeResource challengeResource;

    @RequestMapping(value = "/user/create/{id}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public UserAdapter create(@PathVariable Integer id ,@RequestBody User user) {
        User returned = userDao.create(user);
        if (returned == null) {
            return null;
        }
        return userResource.get(returned.getId());
    }

  @RequestMapping(value = "/user/create/{id}/challenge",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public UserAdapter createChallengeUser(@PathVariable Integer id ,@RequestBody User user) {
        User returned = userDao.create(user);
        if (returned == null) {
            return null;
        }
      return challengeResource.signup(returned.getId());
  }

    @RequestMapping(value = "/user/purge/{id}/{userId}",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.ALL_VALUE)
    public Boolean purge(@PathVariable Integer id ,@PathVariable Integer userId) {
        User u = new User();
        u.setId(userId);
        return userDao.purge(u);
    }

    @RequestMapping(value = "/user/modify", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    public User modify(@RequestBody User user) {
        return userDao.modify(user);
    }
}
