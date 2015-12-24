package com.society.admin.resources;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.util.List;


public class DisplayResource extends BaseController {

    @RequestMapping(value = {"/display/{seasonId}"}, method = RequestMethod.GET)
    public String displaySeasonStandings(@PathVariable String seasonId, Model model, HttpServletResponse response) throws IOException {
        return processDisplay(seasonId, model, null,null);
    }

    private String processDisplay(@NotNull  String seasonId, @NotNull Model model, String teamId, String userId) {
        Season s = seasonApi.get(seasonId);
        List<Team> teamStats = statApi.getSeasonStats(s.getId());
        model.addAttribute("displaySeason",s);
        model.addAttribute("displayTeams");
        if (teamId != null) {
            model.addAttribute("displayMemberStats" ,statApi.getTeamMemberStats(teamId));
            model.addAttribute("displayTeam", teamApi.get(teamId));
        }

        if (userId != null) {
            //model.addAttribute("displayUserResults", playerResultApi.MemberStats(teamId));
            model.addAttribute("displayUser", userApi.get(userId));
        }
        return "display/display";
    }
}
