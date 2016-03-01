package com.society.leagues.resources;

import com.society.leagues.model.ScrambleStatModel;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Stat;
import com.society.leagues.client.api.domain.StatType;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class LeaderResource extends BaseController {

    @RequestMapping(method = RequestMethod.GET, value = "/leaders/{seasonId}")
    public String getLeaders(@PathVariable String seasonId, Model model) {
        List<Stat> stats = statApi.getUserSeasonStats(seasonId)
                .parallelStream()
                .filter(s->s.getUser().isReal())
                .filter(s->s.getTeam() != null)
                .sorted(Stat.sortUserStats())
                .collect(Collectors.toList()
                );

        Season season = seasonApi.get(seasonId);
        Stat newStat = new Stat();
        newStat.setUser(User.defaultUser());
        List<ScrambleStatModel> scrambleStatModelList = new ArrayList<>();
        if (season.isScramble()) {
            for (Stat stat : stats.stream().filter(s->s.getType() == StatType.USER_SEASON).collect(Collectors.toSet())) {
                User u = stat.getUser();
                scrambleStatModelList.add(new ScrambleStatModel(
                        stats.stream().filter(st -> st.getType() == StatType.MIXED_EIGHT).filter(s->s.getUser().equals(u)).findFirst().orElse(newStat),
                        stats.stream().filter(st -> st.getType() == StatType.MIXED_NINE).filter(s->s.getUser().equals(u)).findFirst().orElse(newStat),
                        stats.stream().filter(st -> st.getType() == StatType.USER_SEASON).filter(s->s.getUser().equals(u)).findFirst().orElse(newStat)
                ));
            }

            scrambleStatModelList.sort(new Comparator<ScrambleStatModel>() {
                @Override
                public int compare(ScrambleStatModel o1, ScrambleStatModel o2) {
                    return o2.getPoints().compareTo(o1.getPoints());
                }
            });
            int rank = 0;
            for (ScrambleStatModel scrambleStatModel : scrambleStatModelList) {
                scrambleStatModel.getSeasonStats().setRank(++rank);
            }
            model.addAttribute("stats", scrambleStatModelList);
        } else {
            Stat.getRanks(stats);
            model.addAttribute("stats",stats);
        }
        model.addAttribute("season", season);
        return "leaders/leaders";
    }
}
