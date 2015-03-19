package com.society.test.client;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.UserStats;
import com.society.leagues.resource.client.ChallengeResource;
import com.society.leagues.resource.client.UserResource;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Before;

import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
public class UserClientTest extends TestClientBase {
    @Autowired UserResource userResource;
    @Autowired  ChallengeResource challengeResource;
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
        /*
        User u = userResource.get("login1");
        assertNotNull(u);
        assertNotNull(u.getEmail());
        assertNotNull(u.getFirstName());
        assertNotNull(u.getLastName());
        assertNotNull(u.getLogin());
        assertNull(u.getPassword());
        assertNotNull(u.getPlayerIds());
        assertTrue(u.getPlayerIds().isEmpty());
        */
    }


}
