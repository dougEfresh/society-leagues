package com.society.admin.conf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.admin.LeagueHttpClient;
import com.society.admin.exception.ApiException;
import com.society.admin.exception.UnauthorizedException;
import com.society.admin.security.CookieContext;
import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.Season;
import feign.*;
import feign.codec.ErrorDecoder;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import feign.slf4j.Slf4jLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.annotation.PostConstruct;

@Configuration
public class ClientApiConfig {
    @Autowired ObjectMapper objectMapper;
    JacksonDecoder decoder;
    JacksonEncoder encoder;
    @Value("${rest.url}")
    String restUrl;

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
        return Feign.builder().encoder(encoder).decoder(decoder)
                .logger(new Slf4jLogger())
                .client(new LeagueHttpClient())
                .logLevel(Logger.Level.HEADERS)
                .errorDecoder(new CustomErrorDecoder())
                .requestInterceptor(new HeadersInterceptor())
                .target(clzz, restUrl);
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
        return getApi(TeamApi.class);
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
