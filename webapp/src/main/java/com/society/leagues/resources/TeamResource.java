package com.society.leagues.resources;

import com.society.leagues.model.TeamModel;
import com.society.leagues.client.api.domain.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class TeamResource extends BaseController {

    @RequestMapping(value = {"/team"}, method = RequestMethod.GET)
    public String list(Model model) {
        model.addAttribute("teams",  teamApi.active()
                .stream()
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList()));
        listSeasons(model);
        model.addAttribute("season",Season.getDefault());
        return "team/team";
    }

    @RequestMapping(value = {"/team/season/{id}"}, method = RequestMethod.GET)
    public String listSeason(@PathVariable String id, Model model) {
        model.addAttribute("teams",  teamApi.seasonTeams(id)
                .stream()
                .filter(t->t.getSeason().equals(new Season(id)))
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                .collect(Collectors.toList()));
        listSeasons(model);
        model.addAttribute("season",seasonApi.get(id));
        return "team/team";
    }

    private void listSeasons(Model model) {
        List<Season> seasons = new ArrayList<>();
        Season defaultSeason = Season.getDefault();
        defaultSeason.setName("--- Choose ----");
        seasons.add(defaultSeason);
        seasons.addAll(seasonApi.get());
        model.addAttribute("allSeasons", seasons);

    }

    private String processEditTeam(String id, Model model) {
        TeamModel tm;
        if (id.equals("new")) {
            tm = TeamModel.fromTeam(new Team());
            tm.setMembers(new TeamMembers());
            tm.setSeason(seasonApi.active().get(0));
        } else {
            tm = TeamModel.fromTeam(teamApi.get(id));
            TeamMembers members = teamApi.members(id);
            tm.setUsers(members.getMembers().stream().filter(User::isReal).collect(Collectors.toList()));
            tm.setMembersId(members.getId());
        }
        model.addAttribute("team", tm);
        listSeasons(model);
        model.addAttribute("season",tm.getSeason());
        return "team/editTeam";
    }

    @RequestMapping(value = {"/team/{id}"}, method = RequestMethod.GET)
    public String edit(@PathVariable String id , Model model) {
        processEditTeam(id,model);
        return "team/editTeam";
    }

    @RequestMapping(value = {"/team/{id}"}, method = RequestMethod.POST)
    public String save(@PathVariable String id, @ModelAttribute("team") TeamModel teamModel, Model model, HttpServletResponse response) {
        if (id.equals("new"))
            teamModel.setId(null);
        TeamMembers members = new TeamMembers(teamModel.getUsers());
        members.setId(teamModel.getMembersId());
        try {
            Team  t = teamApi.save(teamModel);
            teamApi.saveMembers(t.getId(),members);
            return processEditTeam(t.getId(),model);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("save","error");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            model.addAttribute("error",errors.toString());
            return "team/editTeam";
        }
    }
}
