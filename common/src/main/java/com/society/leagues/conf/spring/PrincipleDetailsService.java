package com.society.leagues.conf.spring;

import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserDao;
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

    Logger logger = LoggerFactory.getLogger(PrincipleDetailsService.class);
    @Autowired UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userDao.get(username);
        if (user == null) {
            logger.error("Unknown user: " + username);
            throw new UsernameNotFoundException("Unknown user: " + username);
        }

        List<GrantedAuthority> authorities = new ArrayList<>();
        //TODO Add ROLE_ADMIN ?
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return new org.springframework.security.core.userdetails.User(
                username,
                userDao.getPassword(user.getId()),
                authorities
        );
    }
}
