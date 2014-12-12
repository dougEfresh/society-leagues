package com.society.leagues.api.Account;

import com.society.leagues.domain.CurrentlyLoggedInUser;
import com.society.leagues.domain.DomainUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Component
@RestController
@RequestMapping("/api/v1/account")
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class AccountController {

    @Autowired AccountDao dao;

    @ResponseBody
    @RequestMapping(value = "/info",method = RequestMethod.GET)
    public Map<String,Object> getAccount(@CurrentlyLoggedInUser DomainUser domainUser) {
        return dao.getAcctInfo(domainUser.getId());
    }
}
