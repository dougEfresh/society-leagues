package com.society.admin.resources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.HandicapSeason;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class UserResource extends BaseController {

    @Autowired ObjectMapper objectMapper;

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
        model.addAttribute("editUser", u);
        HandicapSeason topGun = u.getActiveHandicapSeasons().stream().filter(s->s.getSeason().isChallenge()).findAny().orElse(null);
        return "user/editUser";
    }

    @RequestMapping(value = {"/user/{id}"}, method = RequestMethod.GET)
    public String edit(@PathVariable String id , Model model) {
        return processEditUser(userApi.get(id),model);
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
}
