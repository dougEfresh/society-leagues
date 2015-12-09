package com.society.admin.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.HandicapSeason;
import com.society.leagues.client.api.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.stream.Collectors;

@Controller
public class UserResource extends BaseController {

    @Autowired ObjectMapper objectMapper;
    @Autowired UserApi userApi;

    @RequestMapping(value = {"/user"}, method = RequestMethod.GET)
    public String list(@RequestParam(defaultValue = "", required = false) String search , Model model) {
        model.addAttribute("search", search);
        if (search.length() > 1)
            model.addAttribute("users", userApi.active().stream().filter(u->u.getName().toLowerCase().contains(search.toLowerCase())).collect(Collectors.toList()));
        else
            model.addAttribute("users", userApi.active());
        return "user/user";
    }

    private String processEditUser(User u, Model model) {
        int i = 0;
        for (HandicapSeason handicapSeason : u.getHandicapSeasons()) {
            handicapSeason.setIndex(i++);
        }
        model.addAttribute("user", u);
        return "user/editUser";
    }

    @RequestMapping(value = {"/user/{id}"}, method = RequestMethod.GET)
    public String edit(@PathVariable String id , Model model) {
        return processEditUser(userApi.get(id),model);
    }

    @RequestMapping(value = {"/user/{id}"}, method = RequestMethod.POST)
    public String save(@PathVariable String id, @ModelAttribute("user") User user, Model model) {
        try {
            User u = userApi.modify(user);
            model.addAttribute("save","success");
            return processEditUser(u, model);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("save","error");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            model.addAttribute("error",errors.toString());
            return processEditUser(user,model);
        }
    }
}
