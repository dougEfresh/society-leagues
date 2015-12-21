package com.society.admin.resources;

import com.society.admin.model.TeamModel;
import com.society.admin.model.UserModel;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class TeamResource extends BaseController {

    @RequestMapping(value = {"/team"}, method = RequestMethod.GET)
    public String list(@RequestParam(defaultValue = "", required = false) String search , Model model) {
        model.addAttribute("search", search);
        if (search.length() > 1) {
            model.addAttribute("teams", teamApi.active()
                    .stream()
                    .filter(u -> u.getName().toLowerCase().contains(search.toLowerCase()))
                    .filter(t->!t.getSeason().isChallenge())
                    .sorted(new Comparator<Team>() {
                        @Override
                        public int compare(Team o1, Team o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    })
                    .collect(Collectors.toList()));
        } else {
            model.addAttribute("teams",  teamApi.active()
                    .stream()
                    .filter(u -> u.getName().toLowerCase().contains(search.toLowerCase()))
                    .filter(t->!t.getSeason().isChallenge())
                    .sorted(new Comparator<Team>() {
                        @Override
                        public int compare(Team o1, Team o2) {
                            return o1.getName().compareTo(o2.getName());
                        }
                    })
                    .collect(Collectors.toList()));
        }

        return "team/team";
    }

    private String processEditTeam(String id, Model model) {
        TeamModel tm = TeamModel.fromTeam(teamApi.get(id));
        TeamMembers members = teamApi.members(id);
        tm.setUsers(members.getMembers().stream().filter(User::isReal).collect(Collectors.toList()));
        tm.setMembersId(members.getId());
        model.addAttribute("team", tm);
        model.addAttribute("allSeasons", seasonApi.get());
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
