package com.society.leagues.Account;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Component
@RestController
@Controller
@RequestMapping("/api/v1/account")
public class AccountController {

    @Autowired JdbcTemplate jdbcTemplate;

    @ResponseBody
    @RequestMapping(value = "/{sessionId}",method = RequestMethod.GET)
    public String getAccount(@PathVariable String sessionId) {
        return "Test";
    }
}
