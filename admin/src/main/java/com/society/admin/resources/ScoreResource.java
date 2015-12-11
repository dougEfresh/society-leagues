package com.society.admin.resources;

import com.society.admin.model.PlayerResultModel;
import com.society.admin.model.TeamMatchModel;
import com.society.leagues.client.api.PlayerResultApi;
import com.society.leagues.client.api.SeasonApi;
import com.society.leagues.client.api.TeamApi;
import com.society.leagues.client.api.TeamMatchApi;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
public class ScoreResource extends BaseController {

    @Autowired TeamMatchApi teamMatchApi;
    @Autowired SeasonApi seasonApi;
    @Autowired TeamApi teamApi;
    @Autowired PlayerResultApi playerResultApi;

    @RequestMapping(value = {"/scores"}, method = RequestMethod.GET)
    public String edit(Model model) {
        model.addAttribute("seasons",seasonApi.active());
        return "scores/index";
    }

    private String processScoreView(String seasonId, String date, String matchId, Model model) {
        model.addAttribute("seasons",seasonApi.active());
        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);

        String d = date == null ? matches.keySet().iterator().next() : date;
        model.addAttribute("dates", matches.keySet());
        model.addAttribute("date", d);
        model.addAttribute("model", new TeamMatchModel(matches.get(d)));
        model.addAttribute("teams", teamApi.getBySeason(seasonId));
        Season s = seasonApi.get(seasonId);
        model.addAttribute("season",s);

        if (matchId != null) {
            PlayerResultModel results = new PlayerResultModel(playerResultApi.getPlayerResultByTeamMatch(matchId),matchId);
            model.addAttribute("results", results);
            Map<String,List<User>> members = teamMatchApi.teamMembers(matchId);
            List<User> home = new ArrayList<>();
            home.add(User.defaultUser());
            List<User> away = new ArrayList<>();
            away.add(User.defaultUser());
            home.addAll(members.get("home"));
            away.addAll(members.get("away"));

            model.addAttribute("teamMatch", results.getPlayerResults().iterator().next().getTeamMatch());
            model.addAttribute("homeMembers", home);
            model.addAttribute("awayMembers", away);
        }

        return "scores/season";
    }

    @RequestMapping(value = {"/scores/{seasonId}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, Model model) {
        return processScoreView(seasonId,null,null,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, @PathVariable String date, Model model) {
        return processScoreView(seasonId,date,null,model);
    }

     @RequestMapping(value = {"/scores/{seasonId}/{date}/add"}, method = RequestMethod.GET)
     public void addTeamMatch(@PathVariable String seasonId, @PathVariable String date, Model model, HttpServletResponse response) throws IOException {
         teamMatchApi.add(seasonId,date);
         response.sendRedirect("/scores/" +seasonId + "/" + date);
    }

     @RequestMapping(value = {"/scores/{seasonId}/{date}/{matchId}/add"}, method = RequestMethod.GET)
     public void addPlayerMatch(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, Model model, HttpServletResponse response) throws IOException {
         teamMatchApi.add(seasonId,date);
         response.sendRedirect("/scores/" +seasonId + "/" + date + "/" + matchId);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}/{matchId}"}, method = RequestMethod.GET)
    public String editResults(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, Model model) {
        return processScoreView(seasonId,date,matchId,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.POST)
    public String save(@PathVariable String seasonId, @PathVariable String date, @ModelAttribute TeamMatchModel teamMatchModel, Model model) {
        return save(seasonId,date,null,teamMatchModel,null,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}/{matchId}"}, method = RequestMethod.POST)
    public String saveResults(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, @ModelAttribute TeamMatchModel teamMatchModel,@ModelAttribute PlayerResultModel playerResultModel, Model model) {
        return save(seasonId,date,matchId,teamMatchModel,playerResultModel,model);
    }
    private String save(String seasonId, String date, String matchId, TeamMatchModel teamMatchModel, PlayerResultModel playerResultModel, Model model) {
        try {
            teamMatchApi.save(teamMatchModel.getMatches());
            if (playerResultModel != null) {
                playerResultModel.getPlayerResults().forEach(
                        r->{
                            if (r.getPlayerHomePartner().equals(User.defaultUser()))
                                r.setPlayerHomePartner(null);

                            if (r.getPlayerAwayPartner().equals(User.defaultUser()))
                                r.setPlayerAwayPartner(null);
                        });
                Season s = seasonApi.get(seasonId);
                if (!s.isChallenge() && !s.isNine()) {
                    for (PlayerResult playerResult : playerResultModel.getPlayerResults()) {
                        if (playerResult.isHomeWinner()) {
                            playerResult.setHomeRacks(playerResult.isScotch() ? 2 : 1);
                            playerResult.setAwayRacks(0);
                        } else {
                            playerResult.setAwayRacks(playerResult.isScotch() ? 2 : 1);
                            playerResult.setHomeRacks(0);
                        }
                    }
                    if (!playerResultModel.getPlayerResults().isEmpty())
                        playerResultApi.save(playerResultModel.getPlayerResults());
                }
            }
            model.addAttribute("save","success");
            return processScoreView(seasonId,date,matchId,model);
        } catch (Exception e) {
            logger.error(e.getMessage(),e);
            model.addAttribute("save","error");
            StringWriter errors = new StringWriter();
            e.printStackTrace(new PrintWriter(errors));
            model.addAttribute("error",errors.toString());
            return processScoreView(seasonId,date,matchId,model);
        }
    }


}