package com.society.leagues.resources;

import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class SignUpResource {
    @Value("${fb.endpoint}")
    String fbEndpoint;
    @Autowired UserApi userApi;

    @RequestMapping(value = "/signup", method = RequestMethod.GET)
    public String signup(Model model) {
        model.addAttribute("fbEndpoint",fbEndpoint);
        model.addAttribute("userSignup",new User());
        return "signup";
    }

    @RequestMapping(value = "/signup", method = RequestMethod.POST)
    public String signup(@RequestParam String email, Model model) {
        User u = new User();
        u.setEmail(email);
        u.setLogin(email);
        userApi.signupFacebook(u);
        return "redirect:/app/home";
    }

}


