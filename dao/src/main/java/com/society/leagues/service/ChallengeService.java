package com.society.leagues.service;

import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChallengeService  {

    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    @Autowired TeamService teamService;
    @Autowired EmailService emailService;

    @PostConstruct
    public void init() {
        refresh();
    }

    public Team createChallengeUser(final User user) {
        Season challenge = leagueService.findAll(Season.class).stream().parallel().filter(s -> s.getDivision().isChallenge()).findFirst().get();
        if (user.getHandicapSeasons().stream().filter(hs->hs.getSeason().getDivision().isChallenge()).count() == 0) {
            user.addHandicap(new HandicapSeason(Handicap.DPLUS,challenge));
        }
        leagueService.save(user);
        return teamService.createTeam(user.getName(),challenge, Arrays.asList(user));
    }

    public void cancel(Challenge c) {
        if (c.getAcceptedSlot() != null) {
            TeamMatch teamMatch = leagueService.findAll(TeamMatch.class).stream().parallel()
                    .filter(tm -> tm.getMatchDate().toLocalDate().isEqual(c.getAcceptedSlot().getLocalDateTime().toLocalDate()))
                    .filter(tm -> tm.getHome().equals(c.getChallenger()) && tm.getAway().equals(c.getOpponent())).findFirst().orElse(null);

            if (teamMatch != null) {
                resultService.removeTeamMatchResult(teamMatch);
            }
        }

        c.setStatus(Status.CANCELLED);
        c.setAcceptedSlot(null);

        leagueService.save(c);
    }

    public TeamMatch accept(Challenge challenge) {
        Team challengerTeam = challenge.getChallenger();
        Team opponentTeam = challenge.getOpponent();
        TeamMatch tm = new TeamMatch(challengerTeam,opponentTeam,challenge.getAcceptedSlot().getLocalDateTime());
        TeamMatch existing = leagueService.findAll(TeamMatch.class).parallelStream()
                .filter(t->t.getSeason().isActive())
                .filter(
                t->t.getHome().equals(challengerTeam) && t.getAway().equals(opponentTeam) && t.getMatchDate().equals(challenge.getAcceptedSlot().getLocalDateTime())
        ).findFirst().orElse(null);
        if (existing != null)
            return existing;

        existing = leagueService.save(tm);
        challenge.setTeamMatch(existing);
        leagueService.save(challenge);
        return existing;
    }

    @Scheduled(fixedRate = 1000*60*60, initialDelay = 1000*60*60)
    public void refresh() {
        LocalDate sunday = LocalDate.now().with(DayOfWeek.SUNDAY);
        final LocalDate now = LocalDate.now();
        LocalDate end = LocalDate.now().plusDays(40);
        List<Slot> slots = leagueService.findAll(Slot.class).stream().parallel().
                filter(s -> s.getLocalDateTime().isAfter(now.atStartOfDay())).
                collect(Collectors.toList());

        while(sunday.isBefore(end)) {
            List<LocalDateTime> defaults = Slot.getDefault(sunday.atStartOfDay());
            final LocalDate challengeDay = sunday;
            List<Slot> slotsOnChallengeDay = slots.stream().filter(s->s.getLocalDateTime().toLocalDate().isEqual(challengeDay)).collect(Collectors.toList());
            for (LocalDateTime time : defaults) {
                if (slotsOnChallengeDay.stream().filter(s->s.getLocalDateTime().equals(time)).count() == 0) {
                    leagueService.save(new Slot(time));
                }
            }
            sunday = sunday.plusDays(7);
        }
        LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
        List<Challenge> accepted = leagueService.findAll(Challenge.class).stream()
                .filter(ch -> ch.getAcceptedSlot() != null)
                .filter(ch -> ch.getChallenger() != null)
                .filter(ch-> ch.getOpponent() != null)
                .filter(ch -> ch.getAcceptedSlot().getLocalDateTime().isAfter(yesterday))
                .filter(ch -> ch.getTeamMatch() == null)
                .collect(Collectors.toList());

        //accepted.forEach(this::accept);
    }
}
