package com.society.leagues.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailTaskRunner implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(EmailTaskRunner.class);
    final EmailSender emailSender;
    final String subject;
    final String body;
    final String to;

    public EmailTaskRunner(EmailSender emailSender, String subject, String body,String to) {
        this.emailSender = emailSender;
        this.subject = subject;
        this.body = body;
        this.to = to;
    }

    @Override
    public void run() {
        try {
            emailSender.email(to,subject,body);
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
    }

    public EmailSender getEmailSender() {
        return emailSender;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getTo() {
        return to;
    }
}
