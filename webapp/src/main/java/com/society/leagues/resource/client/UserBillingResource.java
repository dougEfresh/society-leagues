package com.society.leagues.resource.client;

import com.society.leagues.adapters.BillingAdapter;
import com.society.leagues.email.Email;
import feign.Feign;
import feign.Headers;
import feign.RequestLine;
import feign.jackson.JacksonEncoder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;

@RestController
public class UserBillingResource {

    @Value("${billing-host:localhost}")
    String billingHost = "localhost";

    interface BillingService {
        @RequestLine("GET /api/billing/challenge/initial/{token}/{userId}")
        @Headers("Content-Type: application/json")
        BillingAdapter challengeMembership(String token, Integer userId);
    }
    BillingService service;

    @PostConstruct
    public void init() {
        //service = Feign.builder().logger(new feign.Logger.JavaLogger()).logLevel(feign.Logger.Level.FULL).encoder(new JacksonEncoder()).target(BillingService.class,"http://" + billingHost);
    }

    @RequestMapping(value = "/api/user/challenge/membership/{token}/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BillingAdapter challengeMembership(String token, Integer userId) {
        return null;
    }
}
