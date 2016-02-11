package com.society.leagues.resources;

import com.society.leagues.client.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class SignUpResource {
    @Value("${fb.endpoint}")
    String fbEndpoint;
    @Autowired UserApi userApi;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("fbEndpoint",fbEndpoint);
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(@RequestBody String email, Model model) {
        model.addAttribute("fbEndpoint",fbEndpoint);
        userApi.signupFacebook(email);
        return "redirect:/app/home";
    }

}


