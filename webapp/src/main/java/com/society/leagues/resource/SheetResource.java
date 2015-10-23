package com.society.leagues.resource;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.service.LeagueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.stream.Collectors;

@Controller
public class SheetResource {

    @Autowired LeagueService leagueService;
    @Value("classpath:eightBallSheetTemplate.html")
    Resource eightBallSheetTemplateResource;
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("d MMM uuuu");

    @PostConstruct
    public void init() throws IOException {
        if (!eightBallSheetTemplateResource.exists()) {
            throw new RuntimeException("resource Doesn't exists");
        }
    }

    @RequestMapping(value = "/api/sheets/{id}",consumes = MediaType.ALL_VALUE)
    public void sheets(@PathVariable String id,HttpServletRequest request, HttpServletResponse response) throws IOException {
        TeamMatch teamMatch = leagueService.findOne(new TeamMatch(id));
        InputStream is = eightBallSheetTemplateResource.getInputStream();
        String eightBallSheetTemplate = StreamUtils.copyToString(is, Charset.defaultCharset());
        eightBallSheetTemplate = eightBallSheetTemplate.replaceAll("%%GAMETYPE%%","8 Ball");
        eightBallSheetTemplate = eightBallSheetTemplate.replaceAll("%%HOME%%",teamMatch.getHome().getName());
        eightBallSheetTemplate = eightBallSheetTemplate.replaceAll("%%AWAY%%",teamMatch.getAway().getName());

        eightBallSheetTemplate = eightBallSheetTemplate.replaceAll("%%DATE%%", formatter.format(teamMatch.getMatchDate()));
        eightBallSheetTemplate = members(teamMatch.getHome(), eightBallSheetTemplate, "%%HOMEMEMBERS%%");
        eightBallSheetTemplate = members(teamMatch.getAway(), eightBallSheetTemplate, "%%AWAYMEMBERS%%");
        response.setContentType("text/html");
        response.getWriter().println(eightBallSheetTemplate);
    }

    private String members(Team team, String template, String key) {
        String members = "";
        for (User user : team.getMembers().stream().sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList())) {
            if (user.getLastName().toLowerCase().contains("handicap") || user.getLastName().toLowerCase().contains("forfeit"))
                continue;

            members += user.getName() + " (" + Handicap.format(user.getHandicap(team.getSeason())) + ")<br>";
        }
        return template.replaceAll(key,members);
    }

    @RequestMapping(value = "/api/sheets/template/eight",consumes = MediaType.ALL_VALUE)
    public void sheetTemplate(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html");
        InputStream is = eightBallSheetTemplateResource.getInputStream();
        String eightBallSheetTemplate = StreamUtils.copyToString(is, Charset.defaultCharset());

        response.getWriter().println(eightBallSheetTemplate);
    }
}
