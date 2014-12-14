package com.society.leagues.api.acount;

import com.society.leagues.domain.BaseController;
import com.society.leagues.domain.annotation.CurrentlyLoggedInUser;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.annotation.RequiresAuth;
import com.society.leagues.domain.annotation.RestAuthController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RequestMapping("/api/v1/account")
@RestAuthController
public class AccountController extends BaseController {

    @Autowired AccountDao dao;

    @RequestMapping(value = "/info",method = RequestMethod.POST)
    public Map<String,Object> getAccount(@CurrentlyLoggedInUser DomainUser domainUser) {
        return dao.getAcctInfo(domainUser.getId());
    }
}
