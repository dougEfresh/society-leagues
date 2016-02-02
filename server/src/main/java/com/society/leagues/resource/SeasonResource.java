package com.society.leagues.resource;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/season")
@SuppressWarnings("unused")
public class SeasonResource {
    @Autowired CacheResource cacheResource;
    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    static Logger logger = LoggerFactory.getLogger(SeasonResource.class);

    @RequestMapping(value = "/{seasonId}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Season get(Principal principal, @PathVariable String seasonId) {
        return leagueService.findOne(new Season(seasonId));
    }

    @RequestMapping(value = "/{seasonId}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Season delete(Principal principal, @PathVariable String seasonId) {
        Season season = leagueService.findOne(new Season(seasonId));
        for (TeamMatch teamMatch : leagueService.findAll(TeamMatch.class)
                .parallelStream().filter(r->r.getSeason().equals(season)).collect(Collectors.toList())) {
            resultService.removeTeamMatchResult(teamMatch);
        }

        for (Team team : leagueService.findAll(Team.class)
                .parallelStream().filter(r->r.getSeason().equals(season)).collect(Collectors.toList())) {
            leagueService.purge(team);
        }
        return season;
    }

    @RequestMapping(value = {"/active","/current"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Season> getActiveSeasons(Principal principal) {
        return leagueService.findAll(Season.class).stream().filter(s->s.isActive()).collect(Collectors.toList());
    }

    @RequestMapping(value = "/create/schedule/{seasonId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public List<TeamMatch> schedule(Principal principal, @PathVariable String seasonId) {
        Season season = leagueService.findOne(new Season(seasonId));
        List<Team> teams = leagueService.findAll(Team.class).stream()
                .filter(t -> t.getSeason().equals(season))
                .collect(Collectors.toList());

        teams.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
        List <TeamMatch> existings = leagueService.findAll(TeamMatch.class).stream().filter(tm->season.equals(tm.getSeason())).collect(Collectors.toList());
        for (TeamMatch existing : existings) {
            for( PlayerResult result:  leagueService.findAll(PlayerResult.class).stream()
                    .filter(pr->pr.getTeamMatch() != null)
                    .filter(pr -> existing.equals(pr.getTeamMatch())).collect(Collectors.toList())) {
                leagueService.purge(result);
            }
            leagueService.purge(existing);
        }

        List<TeamMatch> matches = new ArrayList<>();

            for(int i = 0; i < teams.size(); i++) {
                Team team = teams.get(i);
                final Team exclude = team;
                List<Team> opponents = teams.stream().filter(t->!t.equals(exclude)).collect(Collectors.toList());
                Team opponent = null;
                for (int week = 0; week < season.getRounds(); week++) {
                LocalDate matchDate = season.getsDate().plusWeeks(week);
                if (matches.stream().filter(m -> m.hasTeam(exclude)
                        && m.getMatchDate().toLocalDate().equals(matchDate)).count() > 0) {
                    //Already has match for that day
                    logger.info(String.format("Skipping %s", team.getName()));
                    continue;
                }
                int j = 0;
                do {
                    final Team op;
                    if (week % 2 == 0)
                        op = opponents.get((week + j) % (opponents.size()));
                    else
                        op = opponents.get(( Math.abs(week - opponents.size())) % (opponents.size()));
                    j++;
                    //opponent is already playing this week
                    if (matches.stream()
                            .filter(m->m.getMatchDate().toLocalDate().equals(matchDate))
                            .filter(m->m.hasTeam(op)).count() > 0) {
                        continue;
                    }
                    //They played each other previous week
                    if (matches.stream()
                            .filter(m->m.getMatchDate().toLocalDate().equals(matchDate.minusWeeks(1)))
                            .filter(m->m.hasTeam(exclude) && m.hasTeam(op)).count() == 0) {
                        opponent = op;
                    }

                } while(opponent == null && (j+1) <= opponents.size()*2);

                if (opponent == null) {
                    logger.warn("No match for team:  " + team.getName() + "  "+ week);
                }
                if (opponent != null) {
                    logger.info(String.format("%s vs %s (%s)", team.getName(), opponent.getName(), matchDate.toString()));

                    TeamMatch existing = matches.stream().filter(m -> m.hasTeam(exclude)).sorted(new Comparator<TeamMatch>() {
                        @Override
                        public int compare(TeamMatch o1, TeamMatch o2) {
                            return o2.getMatchDate().compareTo(o1.getMatchDate());
                        }
                    }).findFirst().orElse(null);
                    long homeCnt = matches.stream().filter(m->m.getHome().equals(exclude)).count();
                    long awayCnt = matches.stream().filter(m->m.getAway().equals(exclude)).count();
                    if (homeCnt <= awayCnt) {
                        matches.add(new TeamMatch(team, opponent, matchDate.atStartOfDay()));
                    } else {
                        matches.add(new TeamMatch(opponent, team, matchDate.atStartOfDay()));
                    }
                }
            }
        }
        matches.sort((o1, o2) -> {
            if (o1.getMatchDate().equals(o2.getMatchDate())) {
                return o1.getHome().getName().compareTo(o2.getHome().getName());
            }
            return o1.getMatchDate().compareTo(o2.getMatchDate());
        });
        for (TeamMatch match : matches) {
            logger.info(String.format("Created %s vs %s (%s)", match.getHome().getName(), match.getAway().getName(), match.getDate()));
            leagueService.save(match);
        }

        cacheResource.refresh();

        return matches;
    }

    @RequestMapping(value = "/admin/create", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Season create(Principal principal, @RequestBody Season season) {
        Season previous = leagueService.findAll(Season.class).stream()
                .filter(Season::isActive)
                .filter(s->s.getDivision() == season.getDivision())
                .max((o1, o2) -> o2.getStartDate().compareTo(o2.getStartDate())).get();

        Season newSeason = leagueService.save(season);
        logger.info("Saved new season " + season.getId() + " " + season.getDisplayName());
        List<Team> teams = leagueService.findAll(Team.class).parallelStream().filter(t->t.getSeason().equals(previous)).collect(Collectors.toList());
        List<Team> newTeams =  new ArrayList<>();
        for (Team team : teams) {
            Team newTeam = LeagueObject.copy(team);
            newTeam.setId(null);
            newTeam.setSeason(newSeason);
            newTeams.add(newTeam);
            if (newTeam.getMembers() == null) {
                continue;
            }
            for(User user : newTeam.getMembers().getMembers()) {
                Handicap handicap = user.getHandicap(previous);
                user.addHandicap(new HandicapSeason(handicap,newSeason));
                leagueService.save(user);
            }
            logger.info("Adding team " + newTeam.getName());
        }
        leagueService.save(newTeams);
        return season;
    }

    @RequestMapping(value = "/admin/modify", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Season modify(Principal principal, @RequestBody Season season) {
        return leagueService.save(season);
    }

    @RequestMapping(value = {"/get","/",""}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Season> getSeasons(Principal principal) {
        return leagueService.findAll(Season.class).stream().filter(s->s.getDivision() != Division.STRAIGHT)
                .sorted(new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o2.getStartDate().compareTo(o1.getStartDate());
            }
        }).collect(Collectors.toList());
    }

    @RequestMapping(value = {"/divisions"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public List<Division> getDivisions(Principal principal) {
        List<Division> divisions = Arrays.asList(Division.values());
        divisions.sort(new Comparator<Division>() {
            @Override
            public int compare(Division division, Division t1) {
                return division.name().compareTo(t1.name());
            }
        });
        return divisions;
    }

    @RequestMapping(value = {"/handicaps"}, method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Handicap[] getHandicaps(Principal principal) {
        return Handicap.values();
    }
}
