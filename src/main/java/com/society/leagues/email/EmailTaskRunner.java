package com.society.leagues.email;

import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailTaskRunner implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(EmailTaskRunner.class);
    final EmailSender emailSender;
    final String subject;
    final String body;
    final User to;

    public EmailTaskRunner(EmailSender emailSender, String subject, String body, User to) {
        this.emailSender = emailSender;
        this.subject = subject;
        this.body = body;
        this.to = to;
    }

    @Override
    public void run() {
        try {

            emailSender.email(to.getEmail(),subject,body);
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
    }
}
