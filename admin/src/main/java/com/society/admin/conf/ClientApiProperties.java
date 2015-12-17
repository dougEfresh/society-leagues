package com.society.admin.conf;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;


@Component
@ConfigurationProperties(prefix = "client.api", ignoreUnknownFields = true)
public class ClientApiProperties {

    String endpoint;
    String team;

    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getTeam() {
        return team == null ? "" : team;
    }

    public void setTeam(String team) {
        this.team = team;
    }

    public boolean teamDebug() {
        return getTeam().equals("debug");
    }
}
