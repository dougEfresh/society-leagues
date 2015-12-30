package com.society.admin.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.admin.LeagueHttpClient;
import com.society.admin.exception.ApiException;
import com.society.admin.exception.UnauthorizedException;
import com.society.admin.security.CookieContext;
import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;

@Configuration
public class ClientApiConfig {
    @Autowired ObjectMapper objectMapper;
    @Autowired ClientApiProperties clientApiProperties;
    @Autowired LeagueHttpClient leagueHttpClient;
    JacksonDecoder decoder;
    JacksonEncoder encoder;

    @PostConstruct
    public void init() {
        decoder = new JacksonDecoder(objectMapper);
        encoder = new JacksonEncoder(objectMapper);
    }

    static class HeadersInterceptor implements RequestInterceptor {
        @Override
        public void apply(RequestTemplate template) {
            SecurityContext context = SecurityContextHolder.getContext();
            if (context instanceof CookieContext && context.getAuthentication().getCredentials() != null)
                template.header("Cookie", context.getAuthentication().getCredentials().toString());
        }
    }

    static class CustomErrorDecoder implements ErrorDecoder {

        @Override
        public Exception decode(String methodKey, Response response) {
            if (response.status() == 401) {
                return new UnauthorizedException(response.status(),response.reason());
            }
            return new ApiException(response.status(), response.reason());
        }
    }

    private <T> T getApi(Class<T> clzz) {
        return getApi(clzz, Logger.Level.BASIC.name());
    }

    private <T> T getApi(Class<T> clzz, String level) {
        return Feign.builder().encoder(encoder).decoder(decoder)
                .logger(new Slf4jLogger())
                .client(leagueHttpClient)
                .logLevel( Logger.Level.valueOf(level))
                .errorDecoder(new CustomErrorDecoder())
                .requestInterceptor(new HeadersInterceptor())
                .target(clzz, clientApiProperties.getEndpoint());
    }

    @Bean
    public StatApi statApi() {
       return getApi(StatApi.class);
    }

    @Bean
    public UserApi userApi() {
       return getApi(UserApi.class);
    }

    @Bean
    public SeasonApi seasonApi() {
        return getApi(SeasonApi.class);
    }

    @Bean
    public TeamApi teamApi() {
        return getApi(TeamApi.class, (clientApiProperties.getTeam()));
    }

    @Bean
    public TeamMatchApi teamMatchApi() {
        return getApi(TeamMatchApi.class);
    }

    @Bean
    public PlayerResultApi playerResultApi() {
        return getApi(PlayerResultApi.class);
    }

}
