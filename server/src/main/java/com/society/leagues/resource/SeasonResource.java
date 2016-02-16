package com.society.leagues.resource;

import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.PlayerResultRepository;
import com.society.leagues.mongo.TeamMatchRepository;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/season")
@SuppressWarnings("unused")
public class SeasonResource {
    @Autowired CacheResource cacheResource;
    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired PlayerResultRepository playerResultRepository;

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

    static  Comparator<TeamMatch> matchSort = (o1, o2) -> o1.getMatchDate().compareTo(o2.getMatchDate());
    static  Comparator<Team> teamSort = (o1, o2) -> o1.getName().compareTo(o2.getName());


    static List<TeamMatch> scheduleSeason(Team[] A, Team[]B, Season season) {
        int aRows = A.length;
        int bRows = B.length;

        List<TeamMatch> matches = new ArrayList<>();
        for(int d = 0; d< aRows; d++) {
            for (int i = 0; i < aRows; i++) { // aRow
                for (int j = 0; j < bRows; j++) { // bColumn
                    Team a = A[i];
                    Team b = B[j];
                    if (a.equals(b)) {
                        continue;
                    }
                    matches.add(new TeamMatch(a, b, season.getStartDate().plusWeeks(d)));
                }
            }
        }
        return matches;
    }

    @RequestMapping(value = "/create/schedule/{seasonId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public Collection<TeamMatch> schedule(Principal principal, @PathVariable String seasonId) {
        Season season = leagueService.findOne(new Season(seasonId));
        List<Team> teams = leagueService.findAll(Team.class).stream()
                .filter(t -> t.getSeason().equals(season))
                .collect(Collectors.toList());

        teams.sort((o1, o2) -> o1.getName().compareTo(o2.getName()));
            List<TeamMatch> existings = leagueService.findAll(TeamMatch.class).stream().filter(tm -> season.equals(tm.getSeason())).collect(Collectors.toList());
            for (TeamMatch existing : existings) {
                for (PlayerResult result : leagueService.findAll(PlayerResult.class).stream()
                        .filter(pr -> pr.getTeamMatch() != null)
                        .filter(pr -> existing.equals(pr.getTeamMatch())).collect(Collectors.toList())) {
                    playerResultRepository.delete(result.getId());
                }
                teamMatchRepository.delete(existing.getId());
            }

        Collection<TeamMatch> allCombos = scheduleSeason(teams.toArray(new Team[]{}),teams.toArray(new Team[]{}),season);
        Random  random = new Random();
        LinkedList<Team> a = new LinkedList<>(teams.subList(0,teams.size()/2));
        LinkedList<Team> b = new LinkedList<>(teams.subList((teams.size()/2),teams.size()));
        Collections.reverse(b);
        List<TeamMatch> matches = new ArrayList<>();
        for (int week = 0 ; week < teams.size()-1 ; week ++ ) {
            for (int i = 0 ; i < a.size(); i++) {
                TeamMatch tm = new TeamMatch(a.get(i), b.get(i), season.getStartDate().plusWeeks(week));
                final Team aTeam = a.get(i);
                long homeCnt = matches.stream().filter(t->t.getHome().equals(aTeam)).count();
                long awayCnt = matches.stream().filter(t->t.getAway().equals(aTeam)).count();
                if (homeCnt <= awayCnt) {
                    tm.setHome(aTeam);
                    tm.setAway(b.get(i));
                } else {
                    tm.setAway(aTeam);
                    tm.setHome(b.get(i));
                }
                matches.add(tm);
            }
            Team l = a.removeLast();
            Team f = b.pop();
            b.addLast(l);
            a.add(1,f);
        }
        int remaining = season.getRounds() - teams.size();
        if (remaining > 1) {
            int begWeek = 0;
            for (int week = season.getRounds() - remaining ; week <= season.getRounds(); week++) {
                LocalDateTime bDate = season.getStartDate().plusWeeks(begWeek++);
                List<TeamMatch> fillerMatches = matches.stream().filter(m->m.getMatchDate().equals(bDate)).collect(Collectors.toList());
                for (TeamMatch fillerMatch : fillerMatches) {
                    logger.info(String.format("Created %s vs %s (%s)", fillerMatch.getAway().getName(), fillerMatch.getHome().getName(), season.getStartDate().plusWeeks(week-1).toLocalDate()));
                    teamMatchRepository.save(new TeamMatch(fillerMatch.getAway(),fillerMatch.getHome(),season.getStartDate().plusWeeks(week-1)));
                }
            }
        }
        for (TeamMatch match : matches.stream().sorted(matchSort).collect(Collectors.toList())) {
            logger.info(String.format("Created %s vs %s (%s)", match.getHome().getName(), match.getAway().getName(), match.getDate()));
            teamMatchRepository.save(match);
        }
        cacheResource.refresh();
        return leagueService.findAll(TeamMatch.class).parallelStream().filter(t->t.getSeason().equals(season)).collect(Collectors.toList());
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
