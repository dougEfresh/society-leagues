package com.society.leagues.client.api.domain;

public class Email {
    String body;
    String to;
    String subject;

    public Email(String to, String subject,String body) {
        this.body = body;
        this.to = to;
        this.subject = subject;
    }

    public Email() {
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}
