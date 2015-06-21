package com.society.leagues.email;

import feign.*;
import feign.jackson.JacksonEncoder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;


@Component
public class EmailSender {
    private static org.slf4j.Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @Value("${email-host:localhost}")
    String emailHost = "localhost";

    interface EmailService {
        @RequestLine("POST /api/email/send")
        @Headers("Content-Type: application/json")
        void send(Email email);
    }
    EmailService service;

    @PostConstruct
    public void init() {
        service = Feign.builder().logger(new feign.Logger.JavaLogger()).logLevel(feign.Logger.Level.FULL).encoder(new JacksonEncoder()).target(EmailService.class,"http://" + emailHost);
    }

    public void email(String recipient, String subject, String body) {
        try {
            service.send(new Email(recipient,subject,body));
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
    }
}
