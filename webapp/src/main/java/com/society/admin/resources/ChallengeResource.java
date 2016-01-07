package com.society.admin.resources;


import com.society.admin.model.ChallengeUserModel;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ChallengeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.GET)
    public String challenge(@RequestParam(required = false) String userId, Model model) throws IOException {
        List<Team> challengeUsers = challengeApi.challengeUsers().stream().filter(user->user.getChallengeUser() != null).collect(Collectors.toList());
        User u = userApi.get();
        Team challenger = challengeUsers.stream().filter(c->c.hasUser(u)).findFirst().orElse(null);
        model.addAttribute("challenger",challenger);
        if (challenger == null)  {
            return "challenge/signup";
        }

        List<ChallengeUserModel> users = ChallengeUserModel.fromUsers(challengeUsers.stream()
                .filter(user->!user.equals(challenger))
                .sorted(new Comparator<Team>() {
            @Override
            public int compare(Team o1, Team o2) {
                return o1.getName().compareTo(o2.getName());
            }
        }).collect(Collectors.toList()));

        if (userId != null) {
            model.addAttribute("opponent",teamApi.get(userId));
        } else {
            model.addAttribute("opponent", User.defaultUser());
        }
        model.addAttribute("challengers", users);
        model.addAttribute("season", seasonApi.active().stream().filter(Season::isChallenge).findFirst().get());

        return "challenge/challenge";
    }
}
