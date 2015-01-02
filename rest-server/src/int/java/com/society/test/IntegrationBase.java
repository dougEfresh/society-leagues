package com.society.test;

import com.society.leagues.resource.ApiResource;
import org.junit.Before;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class IntegrationBase {
    public static final String X_AUTH_USERNAME = "X-Auth-Username";
    public static final String X_AUTH_PASSWORD = "X-Auth-Password";
    public static final String X_AUTH_TOKEN = "X-Auth-Token";

    public RestTemplate restTemplate = new TestRestTemplate();

    @Value("${local.server.port}")
    public int port;

    public String baseURI = "http://localhost";
    String token;

    @Before
    public void setup() {
        try {
            token = getToken();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    public String getToken() throws URISyntaxException {
        String url = String.format("%s:%s/%s?%s=%s&%s=%s",
                baseURI,port, ApiResource.AUTHENTICATE_URL,
                X_AUTH_USERNAME,"email_608@domain.com",
                X_AUTH_PASSWORD,"password_608");

        RequestEntity<String> requestEntity = new RequestEntity<>(
                HttpMethod.POST,
                new URI(url)
        );

        ResponseEntity<HashMap> response =
                restTemplate.postForEntity(new URI(url), requestEntity, HashMap.class);
        assertTrue(response.getStatusCode().is2xxSuccessful());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().containsKey(X_AUTH_TOKEN));
        return (String) response.getBody().get(X_AUTH_TOKEN);
    }

    public Map<String,Object> getRequest(String endpoint,Object ...args) {
        try {
            String url = String.format("%s:%s/%s?%s=%s",
                    baseURI, port, endpoint,
                    X_AUTH_TOKEN, token
            );
            RequestEntity<String> requestEntity = new RequestEntity<>(
                    HttpMethod.POST, new URI(url));
            ResponseEntity<HashMap> response =
                restTemplate.postForEntity(new URI(url), requestEntity, HashMap.class);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertNotNull(response.getBody());
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public List<Map<String,Object>> getRequestList(String endpoint,Object ...args) {
        try {
            String url = String.format("%s:%s/%s?%s=%s",
                    baseURI, port, endpoint,
                    X_AUTH_TOKEN, token
            );
            RequestEntity<String> requestEntity = new RequestEntity<>(
                    HttpMethod.POST, new URI(url));
            ResponseEntity<List> response =
                restTemplate.postForEntity(new URI(url), requestEntity, List.class);
            assertTrue(response.getStatusCode().is2xxSuccessful());
            assertNotNull(response.getBody());
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
