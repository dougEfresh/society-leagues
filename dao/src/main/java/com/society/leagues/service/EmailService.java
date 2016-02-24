package com.society.leagues.service;

import com.society.leagues.client.api.domain.Email;
import feign.*;
import feign.jackson.JacksonEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component
public class EmailService {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Value("${email-host:localhost}")
    String emailHost = "localhost";

    interface EmailServiceApi {
        @RequestLine("POST /api/email/send")
        @Headers("Content-Type: application/json")
        void send(Email email);
    }
    EmailServiceApi service;

    @PostConstruct
    public void init() {
        service = Feign.builder().logger(new feign.Logger.JavaLogger()).logLevel(feign.Logger.Level.FULL).encoder(new JacksonEncoder()).target(EmailServiceApi.class,"http://" + emailHost);
    }

    public void email(String recipient, String subject, String body) {
        try {
            service.send(new Email(recipient,subject,body));
        } catch (Exception t) {
            logger.error(t.getLocalizedMessage());
        }
    }
}
