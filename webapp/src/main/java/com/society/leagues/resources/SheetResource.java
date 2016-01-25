package com.society.leagues.resources;

import com.society.leagues.model.TeamMatchModel;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.TeamMembers;
import com.society.leagues.client.api.domain.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.time.format.DateTimeFormatterBuilder;
import java.time.format.SignStyle;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.time.temporal.ChronoField.DAY_OF_MONTH;
import static java.time.temporal.ChronoField.MONTH_OF_YEAR;
import static java.time.temporal.ChronoField.YEAR;

@Controller
public class SheetResource extends BaseController {
    final static Comparator<User> sortUser = new Comparator<User>() {
        @Override
        public int compare(User o1, User o2) {
            return o1.getName().compareTo(o2.getName());
        }
    };

    @RequestMapping(method = RequestMethod.GET,value = "/sheets/{seasonId}/{date}")
    public String sheets(@PathVariable String seasonId, @PathVariable  String date, Model model) {
        TeamMatchModel teamMatchModel = new TeamMatchModel(teamMatchApi.matchesBySeasonSummary(seasonId).get(date));
        for (TeamMatch teamMatch : teamMatchModel.getMatches()) {
            Map<String,List<User>> users = teamMatchApi.teamMembers(teamMatch.getId());
            teamMatch.getHome().setMembers(new TeamMembers(users.get("home").stream().filter(User::isReal)
                    .sorted(sortUser)
                    .collect(Collectors.toList())));
            teamMatch.getAway().setMembers(new TeamMembers(users.get("away").stream().filter(User::isReal)
                    .sorted(sortUser)
                    .collect(Collectors.toList())));
        }
        model.addAttribute("date", date);
        model.addAttribute("matches", teamMatchModel);
        Season s = seasonApi.get(seasonId);
        for (TeamMatch teamMatch : teamMatchModel.getMatches()) {
            teamMatch.getHome().setMembers(teamApi.members(teamMatch.getHome().getId()));
            teamMatch.getAway().setMembers(teamApi.members(teamMatch.getAway().getId()));
        }
        model.addAttribute("season", s);
        if (s.isScramble())
            return "sheets/scrambleScoreSheets";
        if (s.isChallenge())
            return "sheets/topgunScoreSheets";
        if (s.isNine())
            return "sheets/nineScoreSheets";

        return "sheets/eightScoreSheets";
    }

    @RequestMapping(method = RequestMethod.GET,value = "/sheets/{teamMatchId}")
    public String sheets(@PathVariable String teamMatchId, Model model) {
        TeamMatchModel teamMatchModel = new TeamMatchModel(Arrays.asList(teamMatchApi.get(teamMatchId)));
        for (TeamMatch teamMatch : teamMatchModel.getMatches()) {
            Map<String,List<User>> users = teamMatchApi.teamMembers(teamMatch.getId());
            teamMatch.getHome().setMembers(new TeamMembers(users.get("home").stream().filter(User::isReal)
                    .sorted(sortUser)
                    .collect(Collectors.toList())));
            teamMatch.getAway().setMembers(new TeamMembers(users.get("away").stream().filter(User::isReal)
                    .sorted(sortUser)
                    .collect(Collectors.toList())));
        }
        DateTimeFormatterBuilder builder =  new DateTimeFormatterBuilder()
                .appendValue(YEAR, 4, 10, SignStyle.EXCEEDS_PAD)
                .appendLiteral('-')
                .appendValue(MONTH_OF_YEAR, 2)
                .appendLiteral('-')
                .appendValue(DAY_OF_MONTH, 2);

        model.addAttribute("date", teamMatchModel.getMatches().iterator().next().getMatchDate().format(builder.toFormatter()));
        model.addAttribute("matches", teamMatchModel);
        Season s = teamMatchModel.getMatches().iterator().next().getSeason();
        model.addAttribute("season", s);
        if (s.isScramble())
            return "sheets/scrambleScoreSheets";
        if (s.isChallenge())
            return "sheets/challengeScoreSheets";
        if (s.isNine())
            return "sheets/nineScoreSheets";

        return "sheets/eightScoreSheets";
    }
}
