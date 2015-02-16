package com.society.leagues.resource;

import com.society.leagues.client.api.AuthApi;
import com.society.leagues.client.api.domain.Login;
import com.society.leagues.client.api.domain.TokenResponse;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.infrastructure.security.ServiceAuthenticator;
import com.society.leagues.infrastructure.security.UserSecurityContext;
import com.society.leagues.infrastructure.token.TokenService;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class AuthResource extends ApiResource implements AuthApi {
    final static Logger logger = LoggerFactory.getLogger(AuthResource.class);
    @Autowired ServiceAuthenticator authenticator;
    @Autowired TokenService tokenService;

    @Override
    public TokenResponse authenticate(Login user) {
        if (user == null)
            return new TokenResponse();

         return auth(user.getUsername(),user.getPassword());
    }

    private TokenResponse auth(String username, String password) {
        TokenResponse response = new TokenResponse();
        response.setSuccess(false);
        try {
            User authUser = authenticator.authenticate(username,password);
            if (authUser == null)
                return response;

            String token = tokenService.generateNewToken();
            tokenService.store(token, new UserSecurityContext(authUser));
            response.setId(authUser.getId());
            response.setToken(token);
            response.setSuccess(true);
        } catch (Throwable t) {
            logger.error("Error occurred during auth", t);
        }
        return response;
    }

}
