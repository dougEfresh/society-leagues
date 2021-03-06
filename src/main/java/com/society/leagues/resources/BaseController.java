package com.society.leagues.resources;

import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.*;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.annotation.PostConstruct;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;


public class BaseController {
    static Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired TeamMatchApi teamMatchApi;
    @Autowired SeasonApi seasonApi;
    @Autowired TeamApi teamApi;
    @Autowired PlayerResultApi playerResultApi;
    @Autowired UserApi userApi;
    @Autowired StatApi statApi;

    Season adminSeason;
    @Autowired Environment environment;
    boolean dev = true;

    @PostConstruct
    public void init(){
        if (environment.acceptsProfiles("dev"))
            dev = false;
    }

    public User getUser(Model model) {
        return model.asMap().containsKey("user") ? (User) model.asMap().get("user") : User.defaultUser();
    }

    @ModelAttribute
    public void setModels(Model model, HttpServletRequest request, ResponseFacade response) {
        model.addAttribute("tracking",dev);
        User user = userApi.get();
        model.addAttribute("realUser",user);
        List<Season> seasons = seasonApi.get();
        RequestFacade requestFacade = (RequestFacade) request;
        adminSeason = seasons.stream().sorted(Season.sortOrder).findFirst().get();
        model.addAttribute("user", user);
        for (Cookie cookie : requestFacade.getCookies()) {
            if (cookie.getName().equals("admin-season")) {
                adminSeason = seasons.stream().filter(s->s.equals(new Season(cookie.getValue()))).findFirst().orElse(adminSeason);
            }
            if (cookie.getName().equals("mimic-user") && user.isAdmin()) {
                model.addAttribute("user", userApi.get(cookie.getValue()));
            }
        }
        model.addAttribute("activeSeasons",seasons.stream().filter(Season::isActive).sorted(Season.sortOrder).collect(Collectors.toList()));
        model.addAttribute("allSeasons",seasons);
        model.addAttribute("challengeSeason",seasons.stream().filter(Season::isChallenge).findFirst().orElse(null));

        model.addAttribute("allUsers", userApi.all().parallelStream().filter(User::isReal).collect(Collectors.toList()));
        model.addAttribute("activeUsers", userApi.all().parallelStream().filter(User::isReal).collect(Collectors.toList()));
        model.addAttribute("adminSeason",adminSeason);
        model.addAttribute("userTeams", teamApi.userTeams(getUser(model).getId()));
    }
}
