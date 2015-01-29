package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.api.MatchApi;
import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class,TestBase.class})
public class MatchClientTest extends TestClientBase implements MatchApi {

    MatchApi api;

    @Before
    public void setup() throws Exception {
        super.setup();
        api = ApiFactory.createApi(MatchApi.class, authenticate(Role.ADMIN), baseURL);
    }

    @Test
    public void testGet() throws Exception {

    }

    @Override
    public Match get(Integer id) {
        return api.get(id);
    }

    @Override
    public List<Match> current(List<User> users) {
        return null;
    }

    @Override
    public List<Match> current(Integer userId) {
        return null;
    }

    @Override
    public List<Match> past(List<User> user) {
        return null;
    }

    @Override
    public List<Match> past(Integer userId) {
        return null;
    }

    @Override
    public List<Match> all(List<User> user) {
        return null;
    }

    @Override
    public List<Match> all(Integer userId) {
        return null;
    }

    @Test
    public void testGetCurrent() throws Exception {
        List<Match> matches = get();
        assertNotNull(matches);
        assertFalse(matches.isEmpty());
    }

    @Override
    public List<Match> get() {
        return api.get();
    }
}