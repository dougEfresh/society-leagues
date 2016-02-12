package com.society.leagues;


import com.society.leagues.mongo.*;
import com.society.leagues.service.*;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.client.api.domain.*;
import io.codearte.jfairy.Fairy;
import io.codearte.jfairy.producer.person.Person;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("unused")
public class ConvertUtil {
    private static Logger logger = Logger.getLogger(ConvertUtil.class);

    @Autowired
    ChallengeService challengeService;
    @Autowired
    LeagueService leagueService;
    @Autowired
    ResultService resultService;
    @Autowired
    PlayerResultRepository playerResultRepository;
    @Autowired
    CacheUtil cacheUtil;
    @Autowired
    TeamService teamService;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;
    @Autowired
    ChallengeRepository challengeRepo;

    public void convertUser() {

        List<PlayerResult> playerResults = leagueService.findAll(PlayerResult.class).stream()
                .filter(p->p.getTeamMatch() != null )
                .filter(p->p.getTeamMatch().getHome() != null && p.getTeamMatch().getAway() != null).collect(Collectors.toList());

        for (PlayerResult playerResult : playerResults) {
            if (!playerResult.getSeason().isActive())
                leagueService.purge(playerResult);
        }
        playerResults = leagueService.findAll(PlayerResult.class).stream()
                .filter(p->p.getTeamMatch() == null||  p.getTeamMatch().getHome() ==  null && p.getTeamMatch().getAway() == null).collect(Collectors.toList());
        for (PlayerResult playerResult : playerResults) {
            leagueService.purge(playerResult);
        }

        List<TeamMatch> teamMatches = leagueService.findAll(TeamMatch.class).stream()
                .filter(p->p.getHome() != null && p.getAway() != null).filter(p->!p.getSeason().isActive())
                .collect(Collectors.toList());

        for (TeamMatch teamMatch : teamMatches) {
            leagueService.purge(teamMatch);
        }

        teamMatches = leagueService.findAll(TeamMatch.class).stream()
                .filter(p->p.getHome() == null || p.getAway() == null)
                .collect(Collectors.toList());
        for (TeamMatch teamMatch : teamMatches) {
            leagueService.purge(teamMatch);
        }

        List<Team> teams = leagueService.findAll(Team.class).stream().filter(t->!t.getSeason().isActive()).collect(Collectors.toList());
        for (Team team : teams) {
            if (team.getMembers() != null && team.getMembers().getId() != null) {
                leagueService.purge(team.getMembers());
            }
            leagueService.purge(team);
        }

        List<User> user = leagueService.findAll(User.class).stream().filter(u->u.isActive()).collect(Collectors.toList());

        for (User user1 : user) {
            leagueService.purge(user1);
        }

        challengeRepo.deleteAll();
        List<Season> seasons = leagueService.findAll(Season.class).stream().filter(s->!s.isActive()).collect(Collectors.toList());

        for (Season season : seasons) {
            leagueService.purge(season);
        }

        /*
        User wrong = leagueService.findByLogin("rtepup@gmail.com");
        User correct = leagueService.findByLogin("rtepub@gmail.com");
        Team wrongTeam = leagueService.findAll(Team.class).stream().filter(t->t.hasUser(wrong)).filter(t->t.isChallenge()).findFirst().get();
        Team correctTeam = leagueService.findAll(Team.class).stream().filter(t->t.hasUser(correct)).filter(t->t.isChallenge()).findFirst().get();

        List<PlayerResult> wrongResults = leagueService.findAll(PlayerResult.class).stream().filter(p->p.hasUser(wrong)).collect(Collectors.toList());
        for (PlayerResult wrongResult : wrongResults) {
            if (wrongResult.getPlayerHome().equals(wrong)) {
                wrongResult.setPlayerHome(correct);
                wrongResult.getTeamMatch().setHome(correctTeam);
            } else {
                wrongResult.setPlayerAway(correct);
                wrongResult.getTeamMatch().setAway(correctTeam);
            }
            leagueService.save(wrongResult.getTeamMatch());
            leagueService.save(wrongResult);
        }

        leagueService.purge(wrongTeam);
        leagueService.purge(wrong);

        List<Challenge> challenges = leagueService.findAll(Challenge.class).stream().filter(c->c.hasTeam(wrongTeam)).collect(Collectors.toList());
        for (Challenge challenge : challenges) {
            leagueService.purge(challenge);
        }
/*
        List<PlayerResult> bad = leagueService.findAll(PlayerResult.class).stream()
                .filter(p->p.getTeamMatch() == null || p.getTeamMatch().getHome() == null || p.getTeamMatch().getAway() == null)
                .collect(Collectors.toList());
        for (PlayerResult result : bad) {
            leagueService.purge(result);
        }


        List<TeamMatch>  badTeam = leagueService.findAll(TeamMatch.class).stream().filter(r->r.getHome() == null || r.getAway() == null).collect(Collectors.toList());
        for (TeamMatch result : badTeam) {
            leagueService.purge(result);
        }
        */
    }
}