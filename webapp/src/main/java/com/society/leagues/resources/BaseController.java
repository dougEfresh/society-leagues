package com.society.leagues.resources;

import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

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

    @ModelAttribute
    public void setModels(Model model) {
        user = userApi.get();
        List<Season> seasons = seasonApi.get();
        model.addAttribute("activeSeasons",seasons.stream().filter(Season::isActive).sorted(Season.sortOrder).collect(Collectors.toList()));
        model.addAttribute("allSeasons",seasons);
        model.addAttribute("challengeSeason",seasons.stream().filter(Season::isChallenge).findFirst().orElse(null));
        model.addAttribute("user", user);
        model.addAttribute("userTeams", teamApi.userTeams(user.getId()));
        model.addAttribute("allUsers", userApi.all().parallelStream().filter(User::isReal).collect(Collectors.toList()));
    }
}
