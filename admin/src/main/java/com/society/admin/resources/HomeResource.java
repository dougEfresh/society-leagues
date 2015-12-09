package com.society.admin.resources;

import com.society.leagues.client.api.UserApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class HomeResource extends BaseController {

    @Autowired UserApi userApi;

    @RequestMapping(value = {"/home","/"}, method = RequestMethod.GET)
    public String home(Model model) {
        model.addAttribute("currentUser", userApi.get());
        return "home";
    }
}
