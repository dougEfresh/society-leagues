package com.society.leagues.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;

import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserResource extends BaseController {

    @Autowired ObjectMapper objectMapper;
    @Autowired ChallengeApi challengeApi;

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
        List<Season> season  = seasonApi.active();
        for (Season s : season) {
            if (u.hasSeason(s))
                continue;
            u.addHandicap(new HandicapSeason(Handicap.NA,s));
        }
        u.setHandicapSeasons(u.getActiveHandicapSeasons());
        model.addAttribute("editUserTeams", teamApi.userTeams(u.getId()).stream().filter(t->t.getSeason().isActive()).collect(Collectors.toList()));
        model.addAttribute("editUser", u);
        Map<String,List<Slot>> times = challengeApi.challengeSlots().parallelStream()
                .collect(Collectors.groupingBy(Slot::getTime)
                );
        model.addAttribute("times",times.keySet().stream().sorted().collect(Collectors.toList()));
        if (user.isChallenge()) {

            model.addAttribute("challengeDisabled",
                    teamApi.userTeams(u.getId())
                            .stream()
                            .filter(t -> t.getSeason().isChallenge())
                            .filter(Team::isDisabled).count() > 0
            );
        }  else {
            model.addAttribute("challengeDisabled",true);
        }

        return "user/editUser";
    }

    @RequestMapping(value = {"/user/{id}"}, method = RequestMethod.GET)
    public String edit(@PathVariable String id , Model model) {
        return processEditUser(userApi.get(id),model);
    }

    @RequestMapping(value = {"/user/delete/fb/profile/{id}"}, method = RequestMethod.GET)
    public String deleteFbPofile(@PathVariable String id , Model model) {
        return processEditUser(userApi.get(id),model);
    }

    @RequestMapping(value = {"/user/disable/challenges/{id}"}, method = RequestMethod.GET)
    public String disableChallenges(@PathVariable String id , Model model) {
        List<Team> team = teamApi.userTeams(id).stream().filter(t->t.getSeason().isChallenge()).collect(Collectors.toList());
        for (Team t : team) {
            t.setDisabled(!t.isDisabled());
            teamApi.save(t);
        }
        return "redirect:/app/user/" + id;
    }


    @RequestMapping(value = {"/user/new"}, method = RequestMethod.GET)
    public String edit(Model model, HttpServletResponse response) {
        User u = User.defaultUser();
        u.setId("new");
        List<Season> season = seasonApi.active();
        for (Season s : season) {
            u.addHandicap(new HandicapSeason(Handicap.NA,s));
        }
        return processEditUser(u,model);
    }

     @RequestMapping(value = {"/user/modify/slots/{id}"}, method = RequestMethod.POST)
     public String disabledSlots(@PathVariable String id , @RequestParam List<LocalTime> disabledSlots , @RequestParam List<LocalTime> broadcastSlots, Model model) {
         User u = userApi.get(id);
         u.getUserProfile().setBroadcastSlots(broadcastSlots);
         u.getUserProfile().setDisabledSlots(disabledSlots);
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
            processEditUser(u,model);
            return "redirect:/app/user/" + u.getId();
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
