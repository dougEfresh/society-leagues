package com.society.leagues.resources;

import com.society.leagues.client.api.domain.Season;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SeasonResource extends BaseController {

    @RequestMapping(value = {"/season"}, method = RequestMethod.GET)
    public String season(@RequestParam(required = false) String seasonId, Model model, HttpServletResponse response) {
        List<Season> seasons  = new ArrayList<>();
        seasons.addAll(seasonApi.get().stream().sorted((o1, o2) -> o2.getStartDate().compareTo(o1.getStartDate())).collect(Collectors.toList()));
        model.addAttribute("seasons", seasons);
        if (seasonId != null) {
            if (seasonId.equals("-1") || seasonId.isEmpty()) {
                model.addAttribute("season", Season.getDefault());
            } else {
                Season season = seasonApi.get(seasonId);
                model.addAttribute("season", season);
            }
        }
        return "season/season";
    }

    @RequestMapping(value = {"/season/new"}, method = RequestMethod.GET)
    public String newSeason(Model model, HttpServletResponse response) {
        return season("-1",model,response);
    }


    @RequestMapping(value = {"/season/create/{seasonId}"}, method = RequestMethod.GET)
    public String newSeason(@PathVariable String seasonId, Model model, HttpServletResponse response) {
        seasonApi.schedule(seasonId);
        return season(seasonId,model,response);
    }

    @RequestMapping(value = {"/season"}, method = RequestMethod.POST)
    public String save(Model model, @ModelAttribute Season season, HttpServletResponse response) {
        if (season.getId().equals("-1") || season.getId().isEmpty()) {
            season.setId(null);
            seasonApi.create(season);
        } else {
            seasonApi.modify(season);
        }
        return season(null,model,response);
     }
}
