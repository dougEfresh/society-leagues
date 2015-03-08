package com.society.leagues;

import com.society.leagues.util.Email;
import org.springframework.stereotype.Component;

@Component
public class EmailService {

    Email lastEmailSent;

    public Email sendEmail(Email email) {
        lastEmailSent = email;
        return email;
    }

    public Email getLastEmailSent() {
        return lastEmailSent;
    }

    public void setLastEmailSent(Email lastEmailSent) {
        this.lastEmailSent = lastEmailSent;
    }
}
