package com.society.admin.resources;


import com.society.leagues.client.api.ChallengeApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;

@Controller
public class ChallengeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.GET)
    public String challenge(Model model) throws IOException {
        model.addAttribute("challengers",challengeApi.challengeUsers());
        return "challenge/challenge";
    }
}
