package com.society.leagues.resources;


import com.society.leagues.CopyUtil;
import com.society.leagues.MatchPointService;
import com.society.leagues.model.ChallengeUserModel;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChallengeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;
    @Autowired MatchPointService matchPointService;

    public final static Team broadcast = new Team("-1");
    static {
        broadcast.setName("--- Broadcast ---");
        broadcast.setMembers(new TeamMembers());
        broadcast.addMember(User.defaultUser());
    }

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.GET)
    public String challenge(@RequestParam(required = false) String userId, @RequestParam(required = false) String date, Model  model, HttpServletResponse response) throws IOException {
        return challenge(getUser(model),userId,date,model,response);
    }

    public String challenge(User user , String userId, String date, Model  model, HttpServletResponse response) throws IOException {
        Season s =  seasonApi.active().stream().filter(Season::isChallenge).findFirst().get();
        User u = getUser(model);
        Team challenger = populateTeam( Arrays.asList(teamApi.userTeams(user.getId()).stream().filter(Team::isChallenge).findFirst().orElse(broadcast))).get(0);
        model.addAttribute("challenger",challenger);
        model.addAttribute("season", s);
        processDate(date,userId,model,response);

        List<Challenge> existingChallenges = populateChallenge(challengeApi.challengesForUser(user.getId()));

        model.addAttribute("broadcast", populateChallenge(challengeApi.challenges().stream().filter(Challenge::isBroadcast).collect(Collectors.toList())));
        model.addAttribute("sent",existingChallenges.parallelStream().filter(c-> c.getStatus() != null && c.getStatus(user) == Status.SENT).collect(Collectors.toList()));
        model.addAttribute("pending",existingChallenges.parallelStream().filter(c->c.getStatus() != null && c.getStatus(user) == Status.PENDING).collect(Collectors.toList()));
        model.addAttribute("accepted",existingChallenges.parallelStream().filter(c->c.getStatus() != null && c.getStatus(user) == Status.ACCEPTED).collect(Collectors.toList()));
        Stat stat = statApi.getUserSeasonStats(u.getId(),s.getId()).stream().filter(st->st.getType() == StatType.USER_SEASON).findFirst().get();
        model.addAttribute("currentPoints",stat.getPoints());
        model.addAttribute("virtualWin",virtualWin(u,s,"win"));
        model.addAttribute("virtualHill",virtualWin(u,s,"hill"));
        model.addAttribute("virtualLost",virtualWin(u,s,"lost"));

        List<PlayerResult> playerResults = matchPointService.calcPoints(u,playerResultApi.getResults(user.getId(), s.getId()));
        playerResults.stream().filter(pr->pr.isWinner(u)).count();
        model.addAttribute("win",playerResults.stream().filter(pr->pr.isWinner(u)).count());
        model.addAttribute("lost",playerResults.stream().filter(pr->!pr.isWinner(u)).count());

        if (date != null)
            processUser(userId, date, challenger, model);

        return "challenge/challenge";
    }

    private double virtualWin(User user, Season s, String type) {
        List<PlayerResult> playerResults = playerResultApi.getResults(user.getId(), s.getId());
        LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);
        PlayerResult win =  new PlayerResult();
        TeamMatch tm =  new TeamMatch();
        if (playerResults.isEmpty()) {
            return 0d;
        }
        ReflectionUtils.shallowCopyFieldState(playerResults.get(0),win);
        ReflectionUtils.shallowCopyFieldState(playerResults.get(0).getTeamMatch(),tm);
        win.setTeamMatch(tm);

        win.getTeamMatch().setMatchDate(tomorrow);
        if (win.getPlayerHome().equals(user)) {
            if (type.equals("win")) {
                win.setHomeRacks(7);
                win.setAwayRacks(5);
            }
            if (type.equals("hill")) {
                win.setHomeRacks(6);
                win.setAwayRacks(7);
            }
            if (type.equals("lost")) {
                win.setHomeRacks(5);
                win.setAwayRacks(7);
            }

        } else {
            if (type.equals("win")) {
                win.setAwayRacks(7);
                win.setHomeRacks(5);
            }
            if (type.equals("hill")) {
                win.setHomeRacks(7);
                win.setAwayRacks(6);
            }
            if (type.equals("lost")) {
                win.setHomeRacks(7);
                win.setAwayRacks(5);
            }
        }
        playerResults.add(win);

        playerResults = matchPointService.calcPoints(user,playerResults);
        double mp = 0;
        for (PlayerResult playerResult : playerResults) {
            mp += playerResult.getMatchPoints().getWeightedAvg();
        }
        return mp;
    }


    private List<Challenge> populateChallenge(List<Challenge> challenges) {
        for (Challenge challenge : challenges.stream().filter(c->!c.isBroadcast()).collect(Collectors.toList())) {
            challenge.getChallenger().setMembers(teamApi.members(challenge.getChallenger().getId()));
            challenge.getOpponent().setMembers(teamApi.members(challenge.getOpponent().getId()));
        }
        return challenges;
    }

    @RequestMapping(value = {"/challenge/accept"}, method = RequestMethod.GET)
    public String accept(@RequestParam String id, @RequestParam String slotId, Model model) {
        Challenge ch = challengeApi.challenges().stream().filter(c->c.getId().equals(id)).findAny().get();
        ch.setAcceptedSlot(new Slot(slotId));
        ch.setOpponent(teamApi.userTeams(getUser(model).getId()).stream().filter(Team::isChallenge).findAny().get());
        challengeApi.accept(ch);
        return "redirect:/challenge";
    }

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.POST)
    public String challenge(@RequestParam(required = true) String userId,
                            @RequestParam(required = true) String date,
                            @RequestParam(required = true) String id,
                            @RequestParam String slotIds, Model  model, HttpServletResponse response) throws IOException {
        Challenge challenge = new Challenge();
        challenge.setChallenger(new Team(id));
        List<Slot> slots = new ArrayList<>();
        for (String s : slotIds.split(",")) {
            slots.add(new Slot(s));
        }
        challenge.setSlots(slots);
        if (userId.equals("-1")) {
            challenge.setOpponent(null);
            challenge.setStatus(Status.BROADCAST);
            challengeApi.challenge(challenge);
            return challenge(userId,date,model,response);
        }
        challenge.setOpponent(new Team(userId));
        challenge.setStatus(Status.SENT);
        challengeApi.challenge(challenge);
        return challenge(userId,date,model,response);
    }

    @RequestMapping(value = {"/challenge/cancel/{id}"}, method = RequestMethod.GET)
    public String cancel(@PathVariable String id, Model model, HttpServletResponse response) throws IOException {
        challengeApi.cancel(new Challenge(id));
        return challenge(null,null,model,response);
    }

    @RequestMapping(value = {"/challenge/accept/{id}/{slotId}"}, method = RequestMethod.GET)
    public String accept(@PathVariable String id, @PathVariable String slotId, Model model, HttpServletResponse response) throws IOException {
        return challenge(null,null,model,response);
    }

    private void processDate(String date, String userId, Model model, HttpServletResponse response) throws IOException {
        List<Slot> slots = challengeApi.challengeSlots();
        Team opponent = getOpponent(userId);
        List<LocalDate> dates = slots.stream()
                .map(s->s.getLocalDateTime().toLocalDate())
                .collect(Collectors.toCollection(TreeSet::new))
                .stream().filter(d->!opponent.getChallengeUser().getUserProfile().hasBlockedDate(d.toString())).collect(Collectors.toList());

        Collections.sort(dates, LocalDate::compareTo);
        model.addAttribute("dates", dates);
        model.addAttribute("date",date);
        slots.sort((o1, o2) -> o1.getLocalDateTime().compareTo(o2.getLocalDateTime()));
        model.addAttribute("slots",slots);

        if (date == null) {
            response.sendRedirect(
                    String.format("/app/challenge?date=%s&userId=%s",
                    slots.get(0).getLocalDateTime().toLocalDate(), userId == null ? "-1" : userId
            ));
        }
    }

    private List<Team> populateTeam(List<Team> teams) {
        teams.parallelStream().filter(t->t.equals(broadcast)).forEach(t->t.setMembers(teamApi.members(t.getId())));
        return teams;
    }
    private Team getOpponent(String userId) {
        return userId == null || userId.isEmpty() || broadcast.getId().equals(userId) ? broadcast : populateTeam(Collections.singletonList(teamApi.get(userId))).get(0);
    }
    private void processUser(String userId, String date, Team challenger, Model model) {
        List<Team> challengeUsers = new ArrayList<>();
        LocalDate localDate = LocalDate.parse(date);
        Season challengeSeason = seasonApi.active().stream().filter(Season::isChallenge).findAny().get();
        User user = getUser(model);
        challengeUsers.addAll(
                populateTeam(teamApi.seasonTeams(challengeSeason.getId()).parallelStream()
                        .filter(t->!t.isDisabled()).collect(Collectors.toList())
                ).parallelStream()
                        .filter(t->t.getChallengeUser() != null)
                        .filter(t->!t.getChallengeUser().getUserProfile().hasBlockedDate(date))
                        .filter(t->!t.getChallengeUser().equals(user))
                        .sorted((o1, o2) -> o1.getName().compareTo(o2.getName()))
                        .collect(Collectors.toList())
        );


        Challenge challenge = new Challenge();
        Team opponent = getOpponent(userId);
        challenge.setOpponent(opponent);
        List<Slot> slots =
                challengeApi.challengeSlots().parallelStream()
                        .filter(s->s.getLocalDateTime().toLocalDate().equals(localDate))
                        .filter(s->!opponent.getChallengeUser().getUserProfile().hasBlockedTime(s))
                        .sorted((o1, o2) -> o1.getLocalDateTime().compareTo(o2.getLocalDateTime()))
                        .collect(Collectors.toList());

        challenge.setSlots(slots);
        challenge.setChallenger(challenger);
        model.addAttribute("opponent", opponent);
        model.addAttribute("challengers",  ChallengeUserModel.fromTeams(challengeUsers));
        model.addAttribute("challenge", challenge);
        LocalDate now = LocalDate.now().minusDays(1);
        List<Challenge> sent = challengeApi.challenges().stream()
                .filter(c->c.getStatus() == Status.SENT)
                .filter(c->c.getStatus() != Status.BROADCAST)
                .filter(c-> c.getLocalDate().isAfter(now))
                .collect(Collectors.toList());
        sent.parallelStream().forEach(c->{
            c.getOpponent().setMembers(teamApi.members(c.getOpponent().getId()));
            c.getChallenger().setMembers(teamApi.members(c.getChallenger().getId()));
        } );

        sent.sort(new Comparator<Challenge>() {
            @Override
            public int compare(Challenge o1, Challenge o2) {
                if (o1.getUserChallenger().equals(o2.getUserChallenger())) {
                    return o1.getUserOpponent().getName().compareTo(o2.getUserChallenger().getName());
                }
                return o1.getUserChallenger().getName().compareTo(o2.getUserChallenger().getName());
            }
        });
        model.addAttribute("all",sent);
    }
}
