package com.society.leagues.resources;

import com.society.leagues.model.PlayerResultModel;
import com.society.leagues.model.TeamMatchModel;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ScoreResource extends BaseController {

    @Autowired DisplayResource displayResource;

    @RequestMapping(value = {"/scores"}, method = RequestMethod.GET)
    public String edit(Model model) {

        return "scores/index";
    }

    @RequestMapping(value = {"/scores/{seasonId}"}, method = RequestMethod.GET)
    public String editDate(@PathVariable String seasonId, @RequestParam(required = false, defaultValue = "") String date, Model model) throws IOException {

        Map<String,List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        LocalDateTime now = LocalDateTime.now();

        List<LocalDateTime> dates = new ArrayList<>();
        matches.keySet().forEach(d->dates.add(LocalDate.parse(d).atStartOfDay()));
        LocalDateTime nearest = LocalDate.parse(matches.keySet().iterator().next()).atStartOfDay();
        long distance = Long.MAX_VALUE;
        for (LocalDateTime d: dates) {
            long diff = Math.abs(now.toEpochSecond(ZoneOffset.UTC) - d.toEpochSecond(ZoneOffset.UTC));
            if (diff < distance) {
                distance = diff;
                nearest = d;
            }
        }
        if (date.isEmpty()) {
            return processScoreView(seasonId, nearest.toLocalDate().toString(), null, model);
        } else {
            return processScoreView(seasonId, date, null, model);
        }
    }

    @RequestMapping(value = {"/scores/{seasonId}/add/{date}"}, method = RequestMethod.GET)
    public void addTeamMatch(@PathVariable String seasonId, @PathVariable String date, Model model, HttpServletResponse response) throws IOException {
        teamMatchApi.add(seasonId,date);
        response.sendRedirect("/app/scores/" + seasonId + "?date="+ date);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{matchId}/delete/{date}"}, method = RequestMethod.GET)
    public void deleteTeamMatch(@PathVariable String seasonId,
                                @PathVariable String date,
                                @PathVariable String matchId, Model model, HttpServletResponse response) throws IOException {
        teamMatchApi.delete(matchId);
        response.sendRedirect("/app/scores/" + seasonId + "/" + matchId + "?date=" + date);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{matchId}/add/{date}"}, method = RequestMethod.GET)
     public void addPlayerMatch(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, Model model, HttpServletResponse response) throws IOException {
         playerResultApi.add(matchId);
        response.sendRedirect("/app/scores/" + seasonId + "/" + matchId + "?date=" + date);
    }
    @RequestMapping(value = {"/scores/{seasonId}/{matchId}/add/{date}/four"}, method = RequestMethod.GET)
     public void addFourPlayerMatches(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId, Model model, HttpServletResponse response) throws IOException {
         for(int i =1 ; i <= 4; i++ ) {
             playerResultApi.add(matchId);
         }
        response.sendRedirect("/app/scores/" + seasonId + "/" + matchId + "?date=" + date);
    }
     @RequestMapping(value = {"/scores/{seasonId}/{matchId}/{resultId}/delete/{date}"}, method = RequestMethod.GET)
     public void deletePlayerMatch(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId,
                                   @PathVariable String resultId, Model model, HttpServletResponse response) throws IOException {
         playerResultApi.delete(resultId);
         response.sendRedirect("/app/scores/" +seasonId + "/" + matchId + "?date=" + date);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{matchId}"}, method = RequestMethod.GET)
    public String editResults(@PathVariable String seasonId, @RequestParam(required = false) String date, @PathVariable String matchId, Model model) {
        if (date == null) {
            date = teamMatchApi.get(matchId).getMatchDate().toLocalDate().toString();
        }
        return processScoreView(seasonId,date,matchId,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{date}"}, method = RequestMethod.POST)
    public void save(@PathVariable String seasonId,
                     @PathVariable String date,
                     @ModelAttribute TeamMatchModel teamMatchModel, Model model,
                     HttpServletResponse response) throws IOException {
        response.sendRedirect("/app/scores/" +seasonId +  "?date=" + date);
        save(seasonId,date,null,teamMatchModel,null,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{matchId}/{date}"}, method = RequestMethod.POST)
    public void saveResults(@PathVariable String seasonId, @PathVariable String date, @PathVariable String matchId,
                              @ModelAttribute TeamMatchModel teamMatchModel,@ModelAttribute PlayerResultModel playerResultModel,
                              Model model,
                              HttpServletResponse response) throws IOException {
        response.sendRedirect("/app/scores/" +seasonId + "/" + matchId + "?date=" + date);
        save(seasonId,date,matchId,teamMatchModel,playerResultModel,model);
    }

    @RequestMapping(value = {"/scores/{seasonId}/{matchId}/delete"}, method = RequestMethod.GET)
    public void deleteAll(@PathVariable String seasonId, @PathVariable String matchId,
                          Model model,
                          HttpServletResponse response) throws IOException {
        response.sendRedirect("/app/scores/" +seasonId + "/" + matchId);
        playerResultApi.getPlayerResultsSummary(matchId).forEach(p->playerResultApi.delete(p.getId()));
    }

    private String save(String seasonId, String date, String matchId, TeamMatchModel teamMatchModel, PlayerResultModel playerResultModel, Model model) {
        try {
            if (teamMatchModel != null && teamMatchModel.getMatches() != null && !teamMatchModel.getMatches().isEmpty()) {
                teamMatchApi.save(teamMatchModel.getMatches());
            }
            if (playerResultModel != null) {
                playerResultModel.getPlayerResults().forEach(
                        r->{
                            if (User.defaultUser().equals(r.getPlayerHomePartner()))
                                r.setPlayerHomePartner(null);

                            if (User.defaultUser().equals(r.getPlayerAwayPartner()))
                                r.setPlayerAwayPartner(null);
                        });
                Season s = seasonApi.get(seasonId);
                if (!s.isChallenge() && !s.isNine()) {
                    for (PlayerResult playerResult : playerResultModel.getPlayerResults()) {
                        if (playerResult.isHomeWinner()) {
                            playerResult.setHomeRacks(playerResult.getPlayerHomePartner() != null ? 2 : 1);
                            playerResult.setAwayRacks(0);
                        } else {
                            playerResult.setAwayRacks(playerResult.getPlayerAwayPartner() != null ? 2 : 1);
                            playerResult.setHomeRacks(0);
                        }
                    }
                }
                if (!playerResultModel.getNoForfeits().isEmpty())
                    playerResultApi.save(playerResultModel.getNoForfeits());
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

    private String processScoreView(String seasonId, String date, String matchId, Model model) {
        model.addAttribute("seasons", seasonApi.active());
        Map<String, List<TeamMatch>> matches = teamMatchApi.matchesBySeason(seasonId);
        List<LocalDateTime> dates = new ArrayList<>();
        matches.keySet().forEach(d -> dates.add(LocalDate.parse(d).atStartOfDay()));
        String d = date == null ? matches.keySet().iterator().next() : date;
        model.addAttribute("dates", matches.keySet());
        model.addAttribute("date", d);
        model.addAttribute("model", new TeamMatchModel(seasonApi.get(seasonId),matches.get(d)));
        model.addAttribute("teams", teamApi.seasonTeams(seasonId));
        Season s = seasonApi.get(seasonId);
        model.addAttribute("season", s);

        displayResource.processDisplay(seasonId,model,null,null);

        if (matchId == null) {
            return "scores/index";
        }
        Map<String,List<User>> members = teamMatchApi.teamMembers(matchId);
        if (members.isEmpty()) {
            return "scores/index";
        }
        //List<PlayerResult> realResults =  playerResultApi.getPlayerResultByTeamMatch(matchId);
        List<PlayerResult> realResults =  playerResultApi.getPlayerResultsSummary(matchId);
        realResults.sort(new Comparator<PlayerResult>() {
            @Override
            public int compare(PlayerResult o1, PlayerResult o2) {
                return o1.getMatchNumber().compareTo(o2.getMatchNumber());
            }
        });
        HashSet<PlayerResult> resultsWithForfeits = new HashSet<>();
        int matchNum = 0;
        Iterator<PlayerResult> iterator = realResults.iterator();
        TeamMatch tm = teamMatchApi.get(matchId);
        while(iterator.hasNext()) {
            PlayerResult playerResult = iterator.next();
            matchNum++;
            while(matchNum < playerResult.getMatchNumber()) {
                resultsWithForfeits.add(PlayerResult.addForfeit(matchNum, tm));
                matchNum++;
            }
            matchNum = playerResult.getMatchNumber();
            resultsWithForfeits.add(playerResult);
        }

        List<PlayerResult> sortedList =Arrays.asList(resultsWithForfeits.toArray(new PlayerResult[]{})).stream().sorted(new Comparator<PlayerResult>() {
            @Override
            public int compare(PlayerResult o1, PlayerResult o2) {
                return o1.getMatchNumber().compareTo(o2.getMatchNumber());
            }
        }).collect(Collectors.toList());
        PlayerResultModel results = new PlayerResultModel(sortedList,matchId);
        if (s.isNine()) {
            results = new PlayerResultModel(realResults,matchId);
        }
        model.addAttribute("results", results);
        List<User> home = new ArrayList<>();
        home.add(User.defaultUser());
        List<User> away = new ArrayList<>();
        away.add(User.defaultUser());

        home.addAll(members.get("home")); //.stream().filter(User::isReal).collect(Collectors.toList()));
        away.addAll(members.get("away")); //.stream().filter(User::isReal).collect(Collectors.toList()));
        int homeWins = 0;
        int awayWins = 0;
        int homePoints = 0;
        int awayPoints = 0;

        for (PlayerResult result : results.getPlayerResults()) {
            homeWins += result.getHomeRacks();
            awayWins += result.getAwayRacks();
            homePoints += result.getHomeRacks();
            awayPoints += result.getAwayRacks();
            result.setHomePoints(homePoints);
            result.setAwayPoints(awayPoints);
        }


        TeamMatch m = teamMatchApi.get(matchId);

        //homeWins += m.getHomeForfeits();
        //awayWins += m.getAwayForfeits();
        int homeHandicap = 0;
        int awayHandicap = 0;

        if (homeWins + m.getHomeForfeits() < m.getHomeRacks()) {
            homeHandicap += m.getHomeRacks() - homeWins - m.getHomeForfeits();
        }

        if (awayWins + m.getAwayForfeits() < m.getAwayRacks()) {
            awayHandicap += m.getAwayRacks() - awayWins - m.getAwayForfeits();
        }

        model.addAttribute("homeWins", homeWins);
        model.addAttribute("awayWins", awayWins);
        model.addAttribute("homeForfeits", m.getHomeForfeits());
        model.addAttribute("awayForfeits", m.getAwayForfeits());
        model.addAttribute("homeHandicap", homeHandicap);
        model.addAttribute("awayHandicap", awayHandicap);

        model.addAttribute("homeTotal", homeWins + m.getHomeForfeits() + homeHandicap);
        model.addAttribute("awayTotal", awayWins + m.getAwayForfeits() + awayHandicap);

        model.addAttribute("teamMatch", m);
        model.addAttribute("homeMembers", home);
        model.addAttribute("awayMembers", away);
        return "scores/index";
    }
}
