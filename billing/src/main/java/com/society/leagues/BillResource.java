package com.society.leagues;

import com.society.leagues.adapters.UserAdapter;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.feign.UserRestApi;
import com.stripe.model.Charge;
import com.stripe.Stripe;
import com.stripe.net.RequestOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class BillResource {

    @Autowired
    UserRestApi userRestApi;

    @Value("${stripe-key}")
    String stripeApiKey;

    @RequestMapping(value = "/api/billing/challenge/{token}/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Charge challengePayment(@PathVariable String token, @PathVariable Integer userId) throws Exception {
        UserAdapter u = userRestApi.getUser(userId);

        Stripe.apiKey = stripeApiKey;
        Map<String, Object> chargeParams = new HashMap<String, Object>();
        chargeParams.put("amount", 1500);
        chargeParams.put("currency", "usd");
        chargeParams.put("source", token);
        chargeParams.put("description", "Charge for test@example.com " + userId);
        return Charge.create(chargeParams);
    }

}
