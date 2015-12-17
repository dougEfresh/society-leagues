package com.society.admin.resources;

import com.society.leagues.client.api.*;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletResponse;
import java.util.stream.Collectors;


public class BaseController {
    static Logger logger = LoggerFactory.getLogger(BaseController.class);
    @Autowired TeamMatchApi teamMatchApi;
    @Autowired SeasonApi seasonApi;
    @Autowired TeamApi teamApi;
    @Autowired PlayerResultApi playerResultApi;
    @Autowired UserApi userApi;
    @Autowired StatApi statApi;

    @ModelAttribute
    public void setModels(Model model) {
        model.addAttribute("seasons",seasonApi.active().stream().sorted(Season.sortOrder).collect(Collectors.toList()));
        model.addAttribute("user", userApi.get());
        model.addAttribute("userTeams", teamApi.userTeams());
        model.addAttribute("allUsers", userApi.all().parallelStream().filter(User::isReal).collect(Collectors.toList()));
    }

}
