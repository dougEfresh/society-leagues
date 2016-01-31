package com.society.leagues.resources;

import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import org.apache.catalina.connector.RequestFacade;
import org.apache.catalina.connector.ResponseFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


public class BaseController {
    static Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired TeamMatchApi teamMatchApi;
    @Autowired SeasonApi seasonApi;
    @Autowired TeamApi teamApi;
    @Autowired PlayerResultApi playerResultApi;
    @Autowired UserApi userApi;
    @Autowired StatApi statApi;

    User user;
    Season adminSeason;

    @ModelAttribute
    public void setModels(Model model, HttpServletRequest request, ResponseFacade response) {
        user = userApi.get();
        List<Season> seasons = seasonApi.get();
        RequestFacade requestFacade = (RequestFacade) request;
        adminSeason = seasons.stream().sorted(Season.sortOrder).findFirst().get();
        for (Cookie cookie : requestFacade.getCookies()) {
            if (cookie.getName().equals("admin-season")) {
                adminSeason = seasons.stream().filter(s->s.equals(new Season(cookie.getValue()))).findFirst().orElse(adminSeason);
            }
        }
        model.addAttribute("activeSeasons",seasons.stream().filter(Season::isActive).sorted(Season.sortOrder).collect(Collectors.toList()));
        model.addAttribute("allSeasons",seasons);
        model.addAttribute("challengeSeason",seasons.stream().filter(Season::isChallenge).findFirst().orElse(null));
        model.addAttribute("user", user);
        model.addAttribute("userTeams", teamApi.userTeams(user.getId()));
        model.addAttribute("allUsers", userApi.all().parallelStream().filter(User::isReal).collect(Collectors.toList()));
        model.addAttribute("adminSeason",adminSeason);
    }
}
