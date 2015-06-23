package com.society.leagues;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class BillResource {

    @Value("${stripe-key}")
    String stripApiKey;

    @RequestMapping(value = "/api/v1/billing/challenge/{token}/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String,Object> challengePayment(@PathVariable String token, @PathVariable Integer id) {

	return null;

    }

}
