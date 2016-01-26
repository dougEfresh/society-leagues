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
    @Autowired List<MongoRepository> repos;
    @Autowired SeasonRepository seasonRepository;
    @Autowired TeamMembersRepository teamMembersRepo;
    @Autowired
    TeamRepository teamRepo;
    @Autowired
    SlotRepository slotRepo;

    @PostConstruct
    public void init() {
    //    setUp();
    }

    String defaultPassword = new BCryptPasswordEncoder().encode("abc123");
    public void setUp() {
        for (MongoRepository repo : repos) {
            repo.deleteAll();
        }
        Fairy fairy = Fairy.create();
        for(int i = 0 ; i< 605; i++) {
            Person person = fairy.person();
            User u = new User();
            u.setFirstName(person.firstName());
            u.setLastName(person.lastName());
            u.setLogin(String.format("%s.%s@example.com",u.getFirstName().toLowerCase(),u.getLastName().toLowerCase()));
            u.setEmail(u.getLogin());
            u.setPassword(defaultPassword);
            u.setRole(Role.PLAYER);
            u.setStatus(Status.ACTIVE);
            userRepository.save(u);
        }

        User u = new User();
        u.setFirstName("admin");
        u.setLastName("admin");
        u.setLogin(String.format("%s.%s@example.com",u.getFirstName().toLowerCase(),u.getLastName().toLowerCase()));
        u.setEmail(u.getLogin());
        u.setPassword(defaultPassword);
        u.setRole(Role.ADMIN);
        u.setStatus(Status.ACTIVE);
        userRepository.save(u);

        Season challenge = getSeason(Division.NINE_BALL_CHALLENGE, DayOfWeek.SUNDAY);
        Season scrmable = getSeason(Division.MIXED_MONDAYS_MIXED, DayOfWeek.MONDAY);
        Season tuesday = getSeason(Division.NINE_BALL_TUESDAYS, DayOfWeek.TUESDAY);
        Season wednesday = getSeason(Division.EIGHT_BALL_WEDNESDAYS, DayOfWeek.WEDNESDAY);
        Season thursday = getSeason(Division.EIGHT_BALL_THURSDAYS, DayOfWeek.THURSDAY);

        List<User> users = userRepository.findAll();

        for(int i = 0 ; i<10; i++) {
             TeamMembers tm = new TeamMembers();
            users.get(i).addHandicap(new HandicapSeason(Handicap.DPLUS,challenge));
             tm.addMember(users.get(i));
             tm = teamMembersRepo.save(tm);
             Team t = new Team(challenge,String.format("team%s",(i + 1) + ""));
             t.setMembers(tm);
             teamRepo.save(t);
         }

        createTeam(scrmable,users,100);
        createTeam(tuesday,users,200);
        createTeam(wednesday,users,300);
        createTeam(thursday,users,400);
        /*
        host += ":" + port;
        utils.createAdminUser();
        requestHeaders.add("Cookie", utils.getSessionId(host + "/api/authenticate"));
        requestHeaders.setContentType(MediaType.APPLICATION_JSON);
        */
    }

    private void createTeam(Season season, List<User> users , int start) {
         for(int i = 0 ; i<10; i++) {
             TeamMembers tm = new TeamMembers();
             int j = start + (i * 10);
             int max = j + 10;
             for (; j < max; j++) {
                 User u =  users.get(j);
                 logger.info("Adding " + u.getName());
                 u.addHandicap(new HandicapSeason(season.isNine() ? Handicap.DPLUS : Handicap.FOUR,season));
                 tm.addMember(u);
             }
             tm = teamMembersRepo.save(tm);
             Team t = new Team(season, String.format("team-%s", (start + i) + ""));
             t.setMembers(tm);
             teamRepo.save(t);
         }
    }

    private Season getSeason(Division division, DayOfWeek day) {
        Season s = new Season();
        s.setRounds(10);
        s.setSeasonStatus(Status.ACTIVE);
        s.setDivision(division);
        LocalDate now = LocalDate.now();
        LocalDate startDate = null;
        int cnt = 0;
        do {
            startDate = now.minusDays(cnt);
            if (startDate.getDayOfWeek() == day) {
                s.setsDate(startDate);
            }
            cnt++;
        } while(cnt < 10 && s.getsDate() == null);
        return seasonRepository.save(s);
    }


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
        slotRepo.deleteAll();
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