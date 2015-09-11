package com.society.leagues.test;

import com.society.leagues.CachedCollection;
import com.society.leagues.Main;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
public class TestService {

    @Autowired LeagueService leagueService;
    @Autowired Utils utils;
    @Autowired CachedCollection<List<User>> userCachedCollection;

    @Test
    public void testCache() {
        User u = utils.createRandomUser();
        u.setLogin("new login");
        leagueService.save(u);
    }
}
