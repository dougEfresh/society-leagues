package com.society.leagues;

import com.society.leagues.adapters.BillingAdapter;
import com.society.leagues.client.api.domain.Status;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.billing.Billing;
import com.society.leagues.client.api.domain.billing.BillingPackage;
import com.society.leagues.dao.BillingDao;
import com.society.leagues.dao.UserDao;
import com.stripe.Stripe;
import com.stripe.model.Charge;
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
public class BillingResource {

    @Autowired UserDao userDao;
    @Autowired BillingDao billingDao;

    @Value("${stripe-key:}")
    String stripeApiKey;

    @RequestMapping(value = "/api/billing/challenge/initial/{token}/{userId}",method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public BillingAdapter challengeInitialPayment(@PathVariable String token, @PathVariable Integer userId) throws Exception {
        User user = userDao.get(userId);
        if (user == null) {
            return null;
        }
        Billing billing = new Billing();
        billing.setUser(user);
        billing.setBillingPackage(BillingPackage.CHALLENGE_MEMBERSHIP);
        billing.setStatus(Status.ACCEPTED);
        billing.setAmount(5000);
        if (stripeApiKey == null || stripeApiKey.isEmpty()) {
            return new BillingAdapter(billingDao.create(billing));
        }


        Stripe.apiKey = stripeApiKey;
        Map<String, Object> chargeParams = new HashMap<>();
        chargeParams.put("amount", 1500);
        chargeParams.put("currency", "usd");
        chargeParams.put("source", token);
        chargeParams.put("description", "Charge for test@example.com " + userId);
        Charge ch =  Charge.create(chargeParams);

        billing.setStripeId(ch.getCustomer());

        return new BillingAdapter(billingDao.create(billing));

    }

}
