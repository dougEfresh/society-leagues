package com.society.leagues.resources;


import com.society.leagues.model.ChallengeUserModel;
import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class ChallengeResource extends BaseController {

    @Autowired ChallengeApi challengeApi;

    public static Team broadcast = new Team("-1");
    static {
        broadcast.setName("--- Broadcast ---");
    }

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.GET)
    public String challenge(@RequestParam(required = false) String userId, @RequestParam(required = false) String date, Model  model, HttpServletResponse response) throws IOException {
        processDate(date,userId,model,response);
        Season s =  seasonApi.active().stream().filter(Season::isChallenge).findFirst().get();
        Team challenger = teamApi.userTeams(user.getId()).stream().filter(Team::isChallenge).findFirst().orElse(null);
        if (challenger == null) {
            //ERROR
        }
        model.addAttribute("challenger",challenger);
        model.addAttribute("season", s);
        List<Challenge> existingChallenges = challengeApi.challengesForUser(user.getId());

        model.addAttribute("broadcast", challengeApi.challenges().stream().filter(Challenge::isBroadcast).collect(Collectors.toList()));
        model.addAttribute("sent",existingChallenges.parallelStream().filter(c->c.getStatus(user) == Status.SENT).collect(Collectors.toList()));
        model.addAttribute("pending",existingChallenges.parallelStream().filter(c->c.getStatus(user) == Status.PENDING).collect(Collectors.toList()));
        model.addAttribute("accepted",existingChallenges.parallelStream().filter(c->c.getStatus(user) == Status.ACCEPTED).collect(Collectors.toList()));
        if (date != null)
            processUser(userId, date, challenger, model);

        return "challenge/challenge";
    }

    @RequestMapping(value = {"/challenge"}, method = RequestMethod.POST)
    public String challenge(@RequestParam(required = true) String userId,
                            @RequestParam(required = true) String date,
                            @ModelAttribute Challenge challenge, Model  model, HttpServletResponse response) throws IOException {

        if (userId.equals("-1")) {
            challenge.setOpponent(null);
            challenge.setStatus(Status.BROADCAST);
            challengeApi.challenge(challenge);
            return challenge(userId,date,model,response);
        }
        challenge.setOpponent(new Team(userId));
        challenge.setStatus(Status.SENT);
        return challenge(userId,date,model,response);
    }

    @RequestMapping(value = {"/challenge/cancel/{id}"}, method = RequestMethod.GET)
    public String cancel(@PathVariable String id, Model model, HttpServletResponse response) throws IOException {
        challengeApi.cancel(new Challenge(id));
        return challenge(null,null,model,response);
    }

    @RequestMapping(value = {"/challenge/accept/{id}/{slotId"}, method = RequestMethod.GET)
    public String accept(@PathVariable String id, @PathVariable String slotId, Model model, HttpServletResponse response) throws IOException {

        return challenge(null,null,model,response);
    }

    public void processDate(String date, String userId, Model model, HttpServletResponse response) throws IOException {
        List<Slot> slots = challengeApi.challengeSlots();
        Set<LocalDate> dates = slots.stream()
                .map(s->s.getLocalDateTime().toLocalDate())
                .collect(Collectors.toCollection(TreeSet::new));
        model.addAttribute("dates",dates);
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

    public void processUser(String userId, String date, Team challenger, Model model) {
        User u = userApi.get();
        List<Team> challengeUsers = new ArrayList<>();
        challengeUsers.add(broadcast);
        challengeUsers.addAll(challengeApi.challengeUsersOnDate(date)
                .stream()
                .filter(user->user.getChallengeUser() != null)
                .collect(Collectors.toList()));


        Challenge challenge = new Challenge();
        challenge.setOpponent(new Team(userId));
        if (broadcast.getId().equals(userId)) {
            LocalDate localDate = LocalDate.parse(date);
            challenge.setSlots(challengeApi.challengeSlots().parallelStream()
                    .filter(s->s.getLocalDateTime().toLocalDate().equals(localDate))
                    .sorted((o1, o2) -> o1.getLocalDateTime().compareTo(o2.getLocalDateTime()))
                    .collect(Collectors.toList()));
        } else {
            challenge.setSlots(challengeApi.getAvailableSlotsForUsers(userId,date));
        }
        challenge.setChallenger(challenger);
        List<ChallengeUserModel> users = ChallengeUserModel.fromTeams(challengeUsers.stream()
                .filter(user->!user.equals(challenger))
                .sorted((o1, o2) -> o1.getName().compareTo(o2.getName())).collect(Collectors.toList()));

        model.addAttribute("opponent", userId == null || broadcast.getId().equals(userId) ? broadcast : teamApi.get(userId));
        model.addAttribute("challengers", users);
        model.addAttribute("challenge", challenge);
    }
}
