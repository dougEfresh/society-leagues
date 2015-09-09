package com.society.leagues.resource;


import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ConvertResource {
    private static Logger logger = Logger.getLogger(ConvertResource.class);

    @Autowired UserRepository userRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired PlayerResultRepository playerResultRepository;
    @Autowired JdbcTemplate jdbcTemplate;
    String defaultPassword = new BCryptPasswordEncoder().encode("abc123");
    public void convertUser() {

        logger.info("Convert users");
        userRepository.deleteAll();
        logger.info("Deleted users");
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select\n" +
                " player_id,player_login,first_name,last_name, player_login , password,\n" +
                "  case when player_login like '%no_login%' then 'INACTIVE' else 'ACTIVE' end  status\n" +
                "from player");
        int cnt =0;
        for (Map<String, Object> result : results) {
            User u = new User();
            u.setLegacyId((Integer) (result.get("player_id")));
            u.setLogin(result.get("player_login").toString());
            u.setEmail(result.get("player_login").toString());
            u.setFirstName(result.get("first_name").toString());
            u.setLastName(result.get("last_name").toString());
            u.setStatus(Status.valueOf(result.get("status").toString()));
            if (result.get("password") != null)
                //u.setPassword(new BCryptPasswordEncoder().encode(result.get("password").toString()));
                u.setPassword(defaultPassword);
            else
                u.setPassword(defaultPassword);

            u.setRole(result.get("player_group") == null || result.get("player_group").toString().equals("3") ? Role.PLAYER : Role.ADMIN);

            User user = userRepository.findByLogin(u.getLogin());
            if (user == null) {
                cnt++;
                logger.info("Adding user " + u.getName());
                userRepository.save(u);
            }
        }

        if (cnt != results.size()) {
            throw new RuntimeException("Unable to verify users");
        }
    }

    public Boolean convertSeason() {
        logger.info("Convert season");
        seasonRepository.deleteAll();
        logger.info("Deleted seeasons ");
        List<Map<String,Object>> results = jdbcTemplate.queryForList(
                "select m.season_id,concat(season_year,',',sn_name,',',league_game) as name,\n" +
                        " min(match_start_date) as start_date, max(match_start_date) as end_date\n" +
                        "from \n" +
                        " match_schedule m\n" +
                        " join season s on s.season_id=m.season_id\n" +
                        " join season_name sn on s.season_number=sn.sn_id\n" +
                        " join league l on m.league_id=l.league_id\n" +
                        " where match_start_date > '0000-00-00 00:00:00' " +
                        " group by m.season_id,m.league_id;");
        List<Season> seasons = new ArrayList<>();
        for (Map<String, Object> result : results) {
            Season s = new Season();
            s.setName(result.get("name").toString());
            s.setLegacyId((Integer) result.get("season_id"));
            if (seasonRepository.findByLegacyId(s.getLegacyId()) != null) {
                continue;
            }
            s.setStartDate(LocalDateTime.now());
            if (!s.getName().contains("2015,Summer")) {
                s.setEndDate(LocalDateTime.now());
                s.setSeasonStatus(Status.INACTIVE);
            } else {
                s.setSeasonStatus(Status.ACTIVE);
            }

            if (s.getName().contains("Thurs")) {
                s.setDivision(Division.EIGHT_BALL_THURSDAYS);
            }
            if (s.getName().contains("Wed")) {
                s.setDivision(Division.EIGHT_BALL_WEDNESDAYS);
            }
            if (s.getName().contains("Mixed")) {
                s.setDivision(Division.MIXED_MONDAYS);
            }
            if (s.getName().contains("9-ball")) {
                s.setDivision(Division.NINE_BALL_TUESDAYS);
            }
            if (s.getName().contains("Straight")) {
                s.setDivision(Division.STRAIGHT);
            }

            s = seasonRepository.save(s);
            System.out.println(s.toString());
        }
        return true;
    }

    public void convertTeam() {
        logger.info("Teams ");
        teamRepository.deleteAll();
        logger.info("Delated Teams");

        List<Map<String,Object>> results = jdbcTemplate.queryForList("select * from team_division where season_id is not null and season_id > 66 ");
        List<Season> seasons = seasonRepository.findAll();
        /*
        +-------------+---------+--------------------------------+-----------+--------------+-----------+-----------+--------------+-------------+-------------+---------------+------------+
| division_id | team_id | name  | season_id | division_day | league_id | time_slot | league_game  | league_type | season_year | season_number | start_date
         */
        for (Map<String, Object> result : results) {
            Team t = new Team();
            t.setLegacyId((Integer) result.get("team_id"));
            t.setName(result.get("name").toString());
            Integer id = (Integer) result.get("season_id");
            Season s = seasons.stream().filter(sn->sn.getLegacyId().equals(id)).findFirst().orElse(null);
            if (s == null) {
                throw new RuntimeException("Could not find season " + id);
            }
            t.setSeason(s);
            teamRepository.save(t);
        }
        List<User> users = userRepository.findAll();
        results = jdbcTemplate.queryForList(" select t.*,d.season_id  from team_player t join division d on t.tp_division  = d.division_id where division_id is not null and season_id > 66");
        for (Map<String, Object> result : results) {
            List<Team> teams = teamRepository.findAll();
            Integer id = (Integer) result.get("season_id");
            Integer tid = (Integer) result.get("tp_team");
            Season season = seasons.stream().filter(sn->sn.getLegacyId().equals(id)).findFirst().orElse(null);
            if (season == null) {
                throw new RuntimeException("Could not find season " + id);
            }
            Team team = teams.stream().filter(t -> t.getLegacyId().equals(tid)).filter(t->t.getSeason().getLegacyId().equals(id)).findFirst().orElse(null);
            if (team == null) {
                logger.error("Could not find team " + tid);
                continue;
            }
            Integer pid = (Integer) result.get("tp_player");
            User user = users.stream().filter(u->u.getLegacyId().equals(pid)).findFirst().orElse(null);
            if (user == null) {
                throw new RuntimeException("Could not find user " + pid);
            }
            team.addMember(user);
            teamRepository.save(team);
        }
    }

    public Boolean converTeamMatch() {
        logger.info("Convert team match");
        teamMatchRepository.deleteAll();
        List<Map<String,Object>> matches = jdbcTemplate.
                queryForList("select match_id,m.season_id,home_team_id,visit_team_id,match_start_date " +
                        "from match_schedule m " +
                        "where match_start_date > 2000-01-01 and m.season_id > 66 " +
                        " and  (home_team_id is not null and visit_team_id is not null) order by match_id");

        List<Team> teams  = teamRepository.findAll();

        for (Map<String, Object> match : matches) {
            Integer mid = (Integer) match.get("match_id");
            TeamMatch tm = new TeamMatch();
            tm.setLegacyId(mid);
            Team home = teams.stream().filter(t -> t.getLegacyId().equals(match.get("home_team_id"))).filter(t->t.getSeason().getLegacyId().equals(match.get("season_id"))).findFirst().orElse(null);
            if (home == null) {
                throw  new RuntimeException("no team for " +match.get("home_team_id") );
            }

            Team away = teams.stream().filter(t -> t.getLegacyId().equals(match.get("visit_team_id"))).filter(t->t.getSeason().getLegacyId().equals(match.get("season_id"))).findFirst().orElse(null);

            if (away == null) {
                throw new RuntimeException("no team for " + match.get("visit_team_id"));
            }

            tm.setHome(home);
            tm.setAway(away);
            Timestamp ts = (Timestamp) match.get("match_start_date");
            tm.setMatchDate(ts.toLocalDateTime());
            teamMatchRepository.save(tm);
        }
        return true;
    }
    @RequestMapping(value = "teammatchresult", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Boolean converTeamMatchResult() {
        logger.info("TeamMatch Results");
        List<Map<String,Object>> results =jdbcTemplate.queryForList("select home.match_id,home.racks as home_racks,away.racks as away_racks\n" +
                " from \n" +
                "(select m.match_id,m.home_team_id,\n" +
                "sum(games_won) racks\n" +
                "from  match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.home_team_id = rt.team_id\n" +
                "where m.season_id > 66 \n" +
                "group by m.match_id,m.home_team_id) home \n" +
                "join\n" +
                "   (select m.match_id,m.visit_team_id,\n" +
                "      sum(games_won) as racks  from\n" +
                "    match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.visit_team_id = rt.team_id\n" +
                "    where m.season_id > 66 group by m.match_id,m.visit_team_id\n" +
                "   ) away\n" +
                "on home.match_id=away.match_id");

        List<TeamMatch> teamMatchList = teamMatchRepository.findAll();
        int unmatch = 0 ;
        for (Map<String, Object> result : results) {
            Integer id = (Integer) result.get("match_id");
            BigDecimal h = (BigDecimal) result.get("home_racks");
            BigDecimal a = (BigDecimal) result.get("away_racks");
            TeamMatch tm = teamMatchList.stream().filter(t->t.getLegacyId().equals(id)).findFirst().orElse(null);
            if (tm == null) {
                unmatch++;
                continue;
            }
            tm.setAwayRacks(a.toBigInteger().intValue());
            tm.setHomeRacks(h.toBigInteger().intValue());
            teamMatchRepository.save(tm);
        }
        System.out.println("Matches " + results.size() + " unmatched " + unmatch);
        return Boolean.TRUE;
    }

    public void convertPlayerResults() {
        logger.info("Player Results");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        int errors = 0;
        playerResultRepository.deleteAll();
        List<Map<String,Object>> results = jdbcTemplate.queryForList(
                "select r.*,\n" +
                        "case when h.home_team_id is null then 'away' else 'home' end home_or_away,\n" +
                        "case when h.season_id is null then a.season_id else h.season_id end season_id,\n" +
                        "hc.hcd_name\n" +
                        "from result_ind r\n" +
                        "left join match_schedule h on r.match_id=h.match_id and h.home_team_id=r.team_id\n" +
                        "left join match_schedule a on r.match_id=a.match_id and a.visit_team_id=r.team_id\n" +
                        "LEFT JOIN handicap_display hc ON hc.hcd_id=r.player_handicap " +
                        " where coalesce( h.season_id,0) > 66 or coalesce(a.season_id,0) > 66"
        );
        List<TeamMatch> teamMatchList =  teamMatchRepository.findAll();
        List<User> users = userRepository.findAll();
        List<PlayerResult> playerResults =  new ArrayList<>();
        /*
+-----------+----------+---------+-----------+-----------------+-----------+------------+------
| result_id | match_id | team_id | player_id | player_handicap | games_won | games_lost | match_number | season_id | hcd_name |
+-----------+----------+---------+-----------+-----------------+-----------+------------+------
         */
        for (Map<String, Object> result : results) {
            PlayerResult r =  playerResults.stream().
                    filter(re->re.getTeamMatch() != null).
                    filter(re -> result.get("match_id").equals(re.getTeamMatch().getLegacyId())).
                    filter(re -> result.get("match_number").equals(re.getMatchNumber())).
                    findFirst().orElse(null);

            if (r == null)
                r = new PlayerResult();

            r.setLegacyId((Integer) result.get("result_id"));
            TeamMatch tm = teamMatchList.stream().filter(t->t.getLegacyId().equals((Integer)result.get("match_id"))).findFirst().orElse(null);
            if (tm == null) {
                continue;
            }
            r.setTeamMatch(tm);
            User u  = users.stream().filter(user->user.getLegacyId().equals(result.get("player_id"))).findFirst().orElse(null);
            if (u == null) {
                continue;
            }
            Team home = r.getTeamMatch().getHome();
            Team away = r.getTeamMatch().getAway();

            if (result.get("home_or_away").equals("home")) {
                r.setPlayerHome(u);
                if (result.get("games_won") == null)
                    r.setHomeRacks(0);
                else
                    r.setHomeRacks((Integer) result.get("games_won"));

                r.setHomeRacks((Integer) result.get("games_won"));
                Handicap handicap = Handicap.get(result.get("hcd_name") == null ? null : result.get("hcd_name").toString());
                if (handicap == null) {
                    logger.warn("Handicap " + result.get("hcd_name") + " is null");
                    handicap = Handicap.UNKNOWN;
                }
                r.setPlayerHomeHandicap(handicap);
                home.addMember(u);
            } else {
                r.setPlayerAway(u);
                if (result.get("games_won") == null)
                    r.setAwayRacks(0);
                else
                    r.setAwayRacks((Integer) result.get("games_won"));

                Handicap handicap = Handicap.get(result.get("hcd_name") == null ? Handicap.UNKNOWN.name(): result.get("hcd_name").toString());
                if (handicap == null) {
                    logger.warn("Handicap " + result.get("hcd_name") + " is null");
                    handicap = Handicap.UNKNOWN;
                }
                r.setPlayerAwayHandicap(handicap);
                away.addMember(u);
            }
            r.setMatchNumber((Integer) result.get("match_number"));
            playerResults.add(r);
        }
        errors = 0;
        User handicapUser = userRepository.findAll().stream().filter(u->u.getLastName().equals("HANDICAP")).findFirst().get();
        User forfetUser =  userRepository.findAll().stream().filter(u->u.getLastName().equals("FORFEIT")).findFirst().get();

        for (PlayerResult r : playerResults) {
            if (r.getPlayerAway() == null) {
                r.setPlayerAway(forfetUser);
                r.setAwayRacks(0);
                r.setPlayerAwayHandicap(Handicap.UNKNOWN);

            }
            if (r.getPlayerHome() == null) {
                r.setPlayerHome(forfetUser);
                r.setHomeRacks(0);
                r.setPlayerHomeHandicap(Handicap.UNKNOWN);
            }
                Set<ConstraintViolation<PlayerResult>> constraintViolations = validator.validate(r);
            if (!constraintViolations.isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for (ConstraintViolation<PlayerResult> constraintViolation : constraintViolations) {
                    sb.append(constraintViolation.toString());
                }
                logger.error("Could not validate " + sb.toString());
                errors++;
                //if (errors  > 50) {
                throw new RuntimeException("Too many errors");
                //}
                //continue;
            }
             playerResultRepository.save(r);
        }
        logger.warn("Errors : " + errors);
    }

    public void userHandicap() {
        logger.info("User Handicaps");
        List<PlayerResult> playerResults = playerResultRepository.findAll();
        logger.info("Got player results");
        List<User> users =  userRepository.findAll();
        List<Season> seasons = seasonRepository.findAll();
        for (User user : users) {
            user.getHandicapSeasons().clear();
            List<PlayerResult> userResults = playerResults.stream().filter(r->r.hasUser(user)).collect(Collectors.toList());
            for (Season season : seasons) {
                PlayerResult result = userResults.stream().filter(r->r.getSeason().equals(season)).sorted(new Comparator<PlayerResult>() {
                    @Override
                    public int compare(PlayerResult playerResult, PlayerResult t1) {
                        return t1.getMatchDate().compareTo(playerResult.getMatchDate());
                    }
                }).findFirst().orElse(null);
                if (result == null) {
                    continue;

                }
                HandicapSeason hs = new HandicapSeason(result.getHandicap(user),season);
                user.addHandicap(hs);
            }
            userRepository.save(user);
        }
    }

    public void updateSetWinsLoses() {
        logger.info("Getting  set wins");
        List<PlayerResult> results = playerResultRepository.findAll().stream().filter(r->r.isNine()).collect(Collectors.toList());
        List<TeamMatch> teamMatches = teamMatchRepository.findAll().stream().filter(t->t.getSeason().isNine()).collect(Collectors.toList());
        for (final TeamMatch teamMatch : teamMatches) {
            List<PlayerResult> toProcess = results.stream().filter(r -> r.hasTeam(teamMatch.getHome())).filter(r -> r.getAwayRacks() + r.getHomeRacks() > 0).collect(Collectors.toList());
            for (PlayerResult r : toProcess) {
                if (r.getHomeRacks() > r.getAwayRacks()) {
                    teamMatch.addSetHomeWin();
                } else {
                    teamMatch.addSetHomeLost();
                }
            }
            toProcess = results.stream().filter(r -> r.hasTeam(teamMatch.getAway())).
                    filter(r -> r.getAwayRacks() + r.getHomeRacks() > 0).collect(Collectors.toList());
            for (PlayerResult r : toProcess) {
                if (r.getHomeRacks() > r.getAwayRacks()) {
                    teamMatch.addSetAwayLost();
                } else {
                    teamMatch.addSetAwayWin();
                }
            }
        }
    }
}
