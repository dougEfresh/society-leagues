package com.society.test.client;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.resource.client.UserResource;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserClientTest extends TestClientBase {
    @Autowired UserResource userResource;
    RestTemplate restTemplate;
    
    @Before
    public void setUp() throws Exception {
        super.setup();
        CookieStore cookieStore = new BasicCookieStore();
        HttpClient client = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
        restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory(client));
        /*
        HttpComponentsClientHttpRequestFactory requestFactory =
                (HttpComponentsClientHttpRequestFactory) restTemplate.getRequestFactory();
        DefaultHttpClient httpClient =
                (DefaultHttpClient) requestFactory.getHttpClient();
        httpClient.getCredentialsProvider().setCredentials(
                new AuthScope("localhost", port, AuthScope.ANY_REALM),
                new UsernamePasswordCredentials("name", "pass")
        );
        */
    }

    @Test
    public void testUser() {
        User u = userResource.get("login1");
        assertNotNull(u);
        assertNotNull(u.getEmail());
        assertNotNull(u.getFirstName());
        assertNotNull(u.getLastName());
        assertNotNull(u.getLogin());
        assertNull(u.getPassword());
        assertNotNull(u.getPlayers());
        assertTrue(u.getPlayers().isEmpty());
    }


    @Test
    public void testUserPlayers()  {
        User u = userResource.get("login1");
        List<Player> players = userResource.getUserPlayers(u);
        assertNotNull(players);
        assertFalse(players.isEmpty());
        assertTrue(players.size() == 2);

        assertNotNull(players.get(0).getTeam());
        assertNotNull(players.get(0).getDivision());
        assertNotNull(players.get(0).getSeason());
        assertNotNull(players.get(0).getId());
        assertNotNull(players.get(0).getHandicap());
        assertNotNull(players.get(0).getStart());
    }

    @Test
    public void testPlayers()  {
        List<Player> players = userResource.getPlayers();

        assertNotNull(players);
        assertFalse(players.isEmpty());
        assertTrue(players.size() == 160);

        assertNotNull(players.get(0).getTeam());
        assertNotNull(players.get(0).getDivision());
        assertNotNull(players.get(0).getSeason());
        assertNotNull(players.get(0).getId());
        assertNotNull(players.get(0).getHandicap());
        assertNotNull(players.get(0).getStart());
    }

    @Test
    public void testChallenges()  {
        User u = userResource.get("login1");
        List<Challenge> challenges = userResource.getChallenges(u);

        assertNotNull(challenges);
        assertFalse(challenges.isEmpty());
        assertTrue(challenges.size() > 3);

        assertNotNull(challenges.get(0).getChallengeDate());
        assertNotNull(challenges.get(0).getStatus());
        assertNotNull(challenges.get(0).getTeamMatch());
        assertNotNull(challenges.get(0).getId());

    }

    @Test
    public void testPotentials()  {
        User u = userResource.get("login1");
        Collection<User> users = userResource.getPotentials(u);
        assertNotNull(users);
        assertFalse(users.isEmpty());
        User me = users.stream().filter(user -> user.getId().equals(u.getId())).findFirst().orElse(null);
        assertNull(me);

        List<Player> players = new ArrayList<>();

    }

}
