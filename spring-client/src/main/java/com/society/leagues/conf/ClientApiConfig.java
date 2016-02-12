package com.society.leagues.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.LeagueHttpClient;
import com.society.leagues.exception.ApiException;
import com.society.leagues.exception.UnauthorizedException;
import com.society.leagues.security.CookieContext;
import com.society.leagues.client.api.*;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
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

            // "Accept: application/json, */* ","Content-Type: application/json", "Accept-Encoding: gzip, deflate, sdch"}
            template.header("Accept", MediaType.APPLICATION_JSON_VALUE, MediaType.ALL_VALUE);
            template.header("Content-Type", MediaType.APPLICATION_JSON_VALUE);
            template.header("Accept-Encoding", "gzip", "deflate", "sdch");
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

    public  <T> T getApi(Class<T> clzz) {
        return getApi(clzz, Logger.Level.BASIC.name());
    }

    public <T> T getApi(Class<T> clzz, String level) {
        return  getApi(clzz,level,clientApiProperties.getEndpoint());
    }

    public  <T> T getApi(Class<T> clzz, String level, String endPoint) {

        return Feign.builder().encoder(encoder).decoder(decoder)
                .logger(new Slf4jLogger())
                .client(leagueHttpClient)
                .logLevel( Logger.Level.valueOf(level))
                .errorDecoder(new CustomErrorDecoder())
                .requestInterceptor(new HeadersInterceptor())
                .target(clzz, endPoint);
    }

    @Bean
    public ChallengeApi challengeApiApi() {
       return getApi(ChallengeApi.class);
    }

    @Bean
    public StatApi statApi() {
       return getApi(StatApi.class, clientApiProperties.getStat());
    }

    @Bean
    public UserApi userApi() {
       return getApi(UserApi.class, clientApiProperties.getUser());
    }

    @Bean
    public SeasonApi seasonApi() {
        return getApi(SeasonApi.class, clientApiProperties.getSeason());
    }

    @Bean
    public TeamApi teamApi() {
        return getApi(TeamApi.class, clientApiProperties.getTeam());
    }

    @Bean
    public TeamMatchApi teamMatchApi() {
        return getApi(TeamMatchApi.class, clientApiProperties.getTeamMatch());
    }

    @Bean
    public PlayerResultApi playerResultApi() {
        return getApi(PlayerResultApi.class, clientApiProperties.getPlayerResult());
    }

}
