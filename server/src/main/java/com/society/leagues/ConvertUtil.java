package com.society.leagues;


import com.society.leagues.mongo.UserRepository;
import com.society.leagues.service.*;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.PlayerResultRepository;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import javax.annotation.PostConstruct;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
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

    @PostConstruct
    public void init() {

    }

    String defaultPassword = new BCryptPasswordEncoder().encode("abc123");

    public void convertUser() {
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