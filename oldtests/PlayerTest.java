package com.society.test;

import com.society.leagues.Main;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.stereotype.Component;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.UUID;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class,TestBase.class})
@Component
public class PlayerTest extends TestBase {
    @Autowired PlayerDao api;
    @Autowired SeasonDao seasonApi;
    @Autowired DivisionDao divisionApi;
    @Autowired TeamDao teamApi;
    @Autowired UserDao userApi;

    @Before
    public void setup() throws Exception {
        super.setup();
    }

    @Test
    public void testCreate() {
        Division division = new Division(DivisionType.EIGHT_BALL_THURSDAYS);
        division = divisionApi.create(division);
        assertNotNull(division);

        Season season = new Season("Cool", new Date(),10);
        season.setSeasonStatus(Status.ACTIVE);
        season = seasonApi.create(season);
        assertNotNull(season);

        Team team = new Team(UUID.randomUUID().toString());
        team  = teamApi.create(team);
        assertNotNull(team);

        User user = new User(UUID.randomUUID().toString(),"password");
        user = userApi.create(user);
        assertNotNull(user);

        Player player = new Player(season,user,team,Handicap.D,division);
        player.setStart(new Date());
        player = api.create(player);
        assertNotNull(player);
        assertNotNull(player.getId());

    }
}
