package com.society.leagues.resources;

import com.society.leagues.client.api.domain.Division;
import com.society.leagues.client.api.domain.Season;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class SeasonResource extends BaseController {

    @RequestMapping(value = {"/season"}, method = RequestMethod.GET)
    public String season(@RequestParam(required = false) String seasonId, Model model, HttpServletResponse response) {
        List<Season> seasons  = new ArrayList<>();
        /*Season seasonDefault = Season.getDefault();
        seasonDefault.setName("---- New -----");
        seasons.add(seasonDefault);
        */
        seasons.addAll(seasonApi.get().stream().sorted(Season.sort).collect(Collectors.toList()));
        model.addAttribute("seasons", seasons);
        if (seasonId != null) {
            Season season = seasonApi.get(seasonId);
            model.addAttribute("season",season);
        }
        return "season/season";
    }

    @RequestMapping(value = {"/season"}, method = RequestMethod.POST)
    public String save(Model model, @ModelAttribute Season season, HttpServletResponse response) {
        return season(null,model,response);
     }
}
