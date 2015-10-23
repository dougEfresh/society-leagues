package com.society.leagues.conf.spring;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.mongo.UserRepository;
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
    Logger logger = LoggerFactory.getLogger(PrincipleDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        //TODO Add ROLE_ADMIN ?
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        User u = userRepository.findByLogin(username);
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
