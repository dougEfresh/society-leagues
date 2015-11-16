package com.society.leagues.conf.spring;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.mongo.UserRepository;
import com.society.leagues.service.LeagueService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PrincipleDetailsService implements UserDetailsService {

    @Autowired UserRepository userRepository;
    @Autowired LeagueService leagueService;
    Logger logger = LoggerFactory.getLogger(PrincipleDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        User u = leagueService.findAll(User.class).parallelStream()
                .filter(user -> user.getLogin() != null)
                .filter(
                        user -> user.getLogin().toLowerCase().equals(username.toLowerCase())
                ).findFirst().orElse(null);
        if (u == null) {
            logger.error("Could not find user " + username);
            throw new UsernameNotFoundException("Could not find user " + username);
        }
        u = userRepository.findOne(u.getId());
        if (u.isAdmin()) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }
        return new org.springframework.security.core.userdetails.User(
                username,
                u.getPassword() == null ? "nullpassword" : u.getPassword(),
                authorities
        );
    }
}
