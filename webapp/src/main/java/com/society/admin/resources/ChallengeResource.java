package com.society.admin.resources;


import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.Season;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;

@Controller
public class ChallengeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.GET)
    public String challenge(@RequestParam(required = false) String userId, Model model) throws IOException {
        model.addAttribute("challengers", challengeApi.challengeUsers());
        model.addAttribute("season", seasonApi.active().stream().filter(Season::isChallenge).findFirst().get());

        return "challenge/challenge";
    }
}
