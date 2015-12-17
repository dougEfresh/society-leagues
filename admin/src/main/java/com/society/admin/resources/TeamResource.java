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
        tm.setUsers(teamApi.members(id).stream().filter(User::isReal).collect(Collectors.toList()));
        model.addAttribute("team",tm);
        model.addAttribute("allUsers", userApi.all());
        return "team/editTeam";
    }


    @RequestMapping(value = {"/team/{id}"}, method = RequestMethod.GET)
    public String edit(@PathVariable String id , Model model) {
        processEditTeam(id,model);
        return "team/editTeam";
    }

    @RequestMapping(value = {"/team/{id}"}, method = RequestMethod.POST)
    public String save(@PathVariable String id, @ModelAttribute("team") TeamModel teamModel, Model model, HttpServletResponse response) {
        teamModel.setMembers(new TeamMembers(teamModel.getUsers()));
        teamApi.save()
        return processEditTeam(id,model);
    }

    /*
    @RequestMapping(value = {"/user/new"}, method = RequestMethod.GET)
    public String edit(Model model, HttpServletResponse response) {

        u.setId("new");
        List<Season> season = seasonApi.active();
        for (Season s : season) {
            u.addHandicap(new HandicapSeason(Handicap.NA,s));
        }
        return processEditUser(u,model);
    }

    @RequestMapping(value = {"/user/{id}"}, method = RequestMethod.POST)
    public String save(@PathVariable String id, @ModelAttribute("editUser") User user, Model model, HttpServletResponse response) {
        try {
            if (id.equals("new")) {
                user.setId(null);
            }
            User u = userApi.modify(user);
            model.addAttribute("save","success");
            return processEditUser(u,model);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("save","error");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            model.addAttribute("error",errors.toString());
            return processEditUser(user,model);
        }
    }
    */
}
