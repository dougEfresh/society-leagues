package com.society.leagues.email;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.Properties;

@Component
public class EmailSender {
    @Value("${smtp-host:localhost}") String mailHost;
    @Value("${smtp-port:25}") int mailPort = 25;
    @Value("${mail-from:leagues@societybilliards.com}") String mailFrom;
    @Value("${email-override:}") String emailOverride;
    @Value("${email-only:}") String emailOnly;
    @Value("${email-disable:true}") boolean emailDisabled = true;

    private static Logger logger = LoggerFactory.getLogger(EmailSender.class);

    @PostConstruct
    public void init() {
        logger.info(String.format("smtp-host=%s,smtp-port=%s,mail-from:%s,email-override:%s",mailHost,mailPort,mailFrom,emailOverride));
    }

    public void email(String recipient, String subject, String body) {
        try {
            if (emailOverride != null && !emailOverride.isEmpty()) {
                recipient = emailOverride;
                logger.info("Override email set to : " + emailOverride);
            }
            Session session = getSession();
            MimeMessage message = new MimeMessage(session);
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
            message.setSubject(subject);
            message.setFrom(new InternetAddress(mailFrom));
            MimeBodyPart bodyPart = new MimeBodyPart();
            bodyPart.setText(body);
            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(bodyPart);
            message.setContent(multipart);
            logger.info(String.format("Sending message to %s subject:%s", recipient,subject));
            if (emailDisabled) {
                logger.info("Email sending is disabled");
                return;
            }
            if (emailOnly != null && !emailOnly.isEmpty()) {
                String[] emails = emailOnly.split(",");
                logger.info("Only sending to " + emailOnly);
                for (String email : emails) {
                    if (recipient.equalsIgnoreCase(email)) {
                        Transport.send(message);
                    }
                }
            } else {
                Transport.send(message);
            }
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
    }

    private Session getSession() {
        Properties properties = new Properties();
        properties.put("mail.smtp.host", mailHost);
        properties.put("mail.smtp.port", mailPort);
        return Session.getDefaultInstance(properties);
    }

}
