package com.society.leagues.api.account;

import com.society.leagues.api.ApiController;
import com.society.leagues.domain.annotation.CurrentlyLoggedInUser;
import com.society.leagues.domain.DomainUser;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/account")
@RestController
@Api( value = "/account" ,
        description = "Account Information and Changes ",
        basePath = "/api/vi",
        position = 2
)
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class AccountController extends ApiController {

    @Autowired AccountDao dao;

    @RequestMapping(value = "/info",method = RequestMethod.POST)
    public Map<String,Object> getAccount(@ApiParam() @CurrentlyLoggedInUser DomainUser domainUser) {
        return dao.getAcctInfo(domainUser.getId());
    }
}
