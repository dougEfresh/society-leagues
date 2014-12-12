package com.society.leagues.api.auth;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String authenticate() {
        return "Should never be hit";
    }
}
