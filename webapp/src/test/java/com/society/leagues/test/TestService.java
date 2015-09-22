package com.society.leagues.test;

import com.society.leagues.cache.CachedCollection;
import com.society.leagues.Main;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Main.class})
@WebIntegrationTest
public class TestService {

    @Autowired LeagueService leagueService;
    @Autowired Utils utils;
    @Autowired CachedCollection<List<User>> userCachedCollection;
    @Autowired CachedCollection<List<Season>> seasonCachedCollections;

    @Test
    public void testCache() {
        User u = utils.createRandomUser();
        Season season = leagueService.findAll(Season.class).get(0);
        season.setName("new season");
        final Season newSeason = leagueService.save(season);
        u.getHandicapSeasons().stream().forEach(hs->assertTrue(hs.getSeason().equals(newSeason)));
        u.getHandicapSeasons().stream().forEach(hs->hs.setHandicap(Handicap.UNKNOWN));
        u.getHandicapSeasons().stream().forEach(hs->hs.setSeason(newSeason));
        u.setLogin("new login");
        leagueService.save(u);
        User modifiedUser = userCachedCollection.get().stream().filter(user -> user.getId().equals(u.getId())).findFirst().get();
        assertEquals(u,modifiedUser);
        assertEquals(u.getHandicapSeasons(),modifiedUser.getHandicapSeasons());
        assertTrue(modifiedUser.getHandicapSeasons().stream().allMatch(hs->hs.getHandicap() == Handicap.UNKNOWN));
        assertTrue(modifiedUser.getHandicapSeasons().stream().allMatch(hs->hs.getSeason().getName().equals("new season")));
        assertEquals("new login",modifiedUser.getLogin());

    }
}
