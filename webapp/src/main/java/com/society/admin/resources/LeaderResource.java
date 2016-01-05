package com.society.admin.resources;

import com.society.leagues.client.api.domain.Stat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@Controller
public class LeaderResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/leaders/{seasonId}")
    public String getSchedule(@PathVariable String seasonId, Model model) {
        List<Stat> stats = statApi.getUsersSeasonStats(seasonId);
        model.addAttribute("stats",stats);
        model.addAttribute("season",seasonApi.get(seasonId));
        return "leaders/leaders";
    }
}
