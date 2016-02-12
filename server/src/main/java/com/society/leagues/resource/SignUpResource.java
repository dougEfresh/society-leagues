package com.society.leagues.resource;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.conf.spring.PrincipleDetailsService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices;
import org.springframework.social.connect.web.ProviderSignInUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class SignUpResource {

    @Autowired ProviderSignInUtils providerSignInUtils;
    @Autowired PrincipleDetailsService principleDetailsService;
    @Autowired PersistentTokenBasedRememberMeServices persistentTokenBasedRememberMeServices;
    @Autowired UserService userService;
    @Autowired LeagueService leagueService;

    @RequestMapping(value="/api/signup", method= RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public User signup(@RequestBody User user, WebRequest webRequest, HttpServletRequest request, HttpServletResponse response) {
        UserDetails userDetails = principleDetailsService.loadUserByUsername(user.getLogin().toLowerCase());
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(userDetails.getUsername(),userDetails.getPassword(),userDetails.getAuthorities())
        );
        providerSignInUtils.doPostSignUp(user.getLogin().toLowerCase(), webRequest);
        persistentTokenBasedRememberMeServices.loginSuccess(request, response, SecurityContextHolder.getContext().getAuthentication());
        userService.populateProfile(leagueService.findByLogin(user.getLogin().toLowerCase()));
        return leagueService.findByLogin(user.getLogin().toLowerCase());
    }
}
