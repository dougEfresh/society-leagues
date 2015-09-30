package com.society.leagues.service;

import com.society.leagues.client.api.domain.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ChallengeService  {

    @Autowired LeagueService leagueService;

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

        Team t = leagueService.findAll(Team.class).stream().parallel()
                .filter(team -> team.getMembers().contains(user))
                .filter(team -> team.getSeason().isChallenge())
                .findFirst().orElse(new Team(challenge,user.getName()));
        t.addMember(user);
        return leagueService.save(t);
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
    }
}
