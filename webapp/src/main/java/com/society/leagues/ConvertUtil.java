package com.society.leagues;


import com.society.leagues.Service.ChallengeService;
import com.society.leagues.Service.LeagueService;
import com.society.leagues.Service.ResultService;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.PlayerResultRepository;
import com.society.leagues.resource.PlayerResultResource;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("unused")
public class ConvertUtil {
    private static Logger logger = Logger.getLogger(ConvertUtil.class);

    @Autowired ChallengeService challengeService;
    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    @Autowired PlayerResultRepository playerResultRepository;
    @Autowired CacheUtil cacheUtil;

    @Autowired JdbcTemplate jdbcTemplate;
    String defaultPassword = new BCryptPasswordEncoder().encode("abc123");
    public void convertUser() {
        logger.info("Convert users");
        leagueService.deleteAll(User.class);
        logger.info("Deleted users");
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select\n" +
                " player_id,player_login,first_name,last_name, player_login , player_group,password,\n" +
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

            User user = leagueService.findByLogin(u.getLogin());
            if (user == null) {
                cnt++;
                logger.info("Adding user " + u.getName());
                leagueService.save(u);
            }
        }

        if (cnt != results.size()) {
            throw new RuntimeException("Unable to verify users");
        }
    }

    public Boolean convertSeason() {
        logger.info("Convert season");
        leagueService.deleteAll(Season.class);
        logger.info("Deleted seasons ");
        Season challenge = new Season();
        challenge.setType("Challenge");
        challenge.setSeasonStatus(Status.ACTIVE);
        challenge.setDivision(Division.NINE_BALL_CHALLENGE);
        challenge.setStartDate(LocalDateTime.now().minusDays(90));
        challenge.setLegacyId(4003);
        leagueService.save(challenge);

        List<Map<String,Object>> results = jdbcTemplate.queryForList(
                "select m.season_id," +
                        "concat(season_year,',',sn_name,',',league_game) as name,\n" +
                        " min(coalesce(match_start_date,'1978-04-06')) as start_date, max(match_start_date) as end_date\n" +
                        "from \n" +
                        " match_schedule m\n" +
                        " join season s on s.season_id=m.season_id\n" +
                        " join season_name sn on s.season_number=sn.sn_id\n" +
                        " join league l on m.league_id=l.league_id\n" +
                        " where match_start_date > '0000-00-00 00:00:00' and m.season_id > 1" +
                        " group by m.season_id,m.league_id;");

        for (Map<String, Object> result : results) {
            Season s = new Season();
            String name = (String) result.get("name");
            s.setLegacyId((Integer) result.get("season_id"));
            s.setStartDate(LocalDateTime.now().minusDays(100));
            if (name.contains("2015,Summer")) {
                s.setEndDate(LocalDateTime.now().minusDays(60));
                s.setSeasonStatus(Status.ACTIVE);
            } else {
                s.setSeasonStatus(Status.INACTIVE);
            }
            s.setYear(name.split(",")[0]);
            s.setType(name.split(",")[1]);

            if (name.contains("Thurs")) {
                s.setDivision(Division.EIGHT_BALL_THURSDAYS);
            }
            if (name.contains("Wed")) {
                s.setDivision(Division.EIGHT_BALL_WEDNESDAYS);
            }
            if (name.contains("Mixed")) {
                s.setDivision(Division.MIXED_MONDAYS);
            }
            if (name.contains("9-ball")) {
                s.setDivision(Division.NINE_BALL_TUESDAYS);
            }
            if (name.contains("Straight")) {
                s.setDivision(Division.STRAIGHT);
            }

            s = leagueService.save(s);

            System.out.println(s.toString());
        }
        return true;
    }

    public void convertTeam() {
        logger.info("Teams ");
        leagueService.deleteAll(Team.class);
        logger.info("Delated Teams");
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select distinct season_id,name,team_id " +
                "from ( select distinct season_id,t.name,team_id  from match_schedule s join team t  on s.home_team_id=t.team_id union " +
                "select distinct season_id,t.name,team_id from match_schedule s join team t  on s.visit_team_id=t.team_id) as teams " +
                "where season_id > 1  order by season_id");
        List<Season> seasons = leagueService.findAll(Season.class);
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
            leagueService.save(t);
        }

        System.out.println("Created " + leagueService.findAll(Team.class).size() + " teams");
    }

    public void convertTeamMembers() {
       List<Map<String,Object>> results  = jdbcTemplate.queryForList("select distinct player_id,team_id,season_id  " +
               "from result_ind r join match_schedule m on r.match_id = m.match_id  " +
               "where player_id is not null and team_id is not null and team_id > 0 and player_id > 0");
        List<User> users = leagueService.findAll(User.class);
        List<Team> teams = leagueService.findAll(Team.class);
        Map<Integer,User> userMap = new HashMap<>();
        Map<Integer,Team> teamMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getLegacyId(),user);
        }
        for (Team team : teams) {
            teamMap.put(team.getLegacyId(),team);
        }
        for (Map<String, Object> result : results) {
            Integer id = (Integer) result.get("player_id");
            User u = userMap.get(id);
            HandicapSeason handicapSeason = u.getHandicapSeasons().stream().
                    filter(hs->hs.getSeason().getLegacyId().equals(result.get("season_id"))).
                    findFirst().orElse(null);

            if (handicapSeason == null) {
                continue;
            }
            Team t = teamMap.get((Integer) result.get("team_id"));
            if (t == null) {
                System.err.println("Could not find team " + result.get("team_id"));
                continue;
            }
            t.addMember(u);
            teamMap.put(t.getLegacyId(), leagueService.save(t));
        }
        System.out.print("Found  " + results.size() + " teams with memebers ");
    }

    public Boolean converTeamMatch() {
        logger.info("Convert team match");
        leagueService.deleteAll(TeamMatch.class);
        List<Map<String,Object>> matches = jdbcTemplate.
                queryForList("select match_id,m.season_id,home_team_id,visit_team_id,match_start_date " +
                        "from match_schedule m " +
                        "where match_start_date is not null and match_start_date > 2000-01-01 and m.season_id > 0 " +
                        " and  (home_team_id is not null and visit_team_id is not null) order by match_id");

        List<Team> teams  = leagueService.findAll(Team.class);
        int unmatched = 0;
        for (Map<String, Object> match : matches) {
            Integer mid = (Integer) match.get("match_id");
            TeamMatch tm = new TeamMatch();
            tm.setLegacyId(mid);
            Team home = teams.stream().filter(t ->
                    t.getLegacyId().equals(match.get("home_team_id"))).
                    filter(t -> t.getSeason().getLegacyId().equals(match.get("season_id"))).
                    findFirst().orElse(null);

            if (home == null) {
                unmatched++;
                continue;
            }

            Team away = teams.stream().filter(t ->
                    t.getLegacyId().equals(match.get("visit_team_id"))).
                    filter(t -> t.getSeason().getLegacyId().equals(match.get("season_id"))).
                    findFirst().orElse(null);

            if (away == null) {
                unmatched++;
                continue;
            }

            tm.setHome(home);
            tm.setAway(away);
            Timestamp ts = (Timestamp) match.get("match_start_date");
            tm.setMatchDate(ts.toLocalDateTime());
            leagueService.save(tm);
        }
        System.err.println("Unmatched teamMatch " + unmatched);
        return true;
    }

    public Boolean converTeamMatchResult() {
        logger.info("TeamMatch Results");
        List<Map<String,Object>> results =jdbcTemplate.queryForList(" select * from result_team");
        List<TeamMatch> teamMatchList = leagueService.findAll(TeamMatch.class);
        Map<Integer,TeamMatch> teamMatchMap = new HashMap<>();
        for (TeamMatch teamMatch : teamMatchList) {
            teamMatchMap.put(teamMatch.getLegacyId(), teamMatch);
        }
        int unmatch = 0 ;
        for (Map<String, Object> result : results) {
            Integer id = (Integer) result.get("match_id");
            Integer tid = (Integer) result.get("team_id");
            Integer win = (Integer) result.get("is_win");
            TeamMatch tm = teamMatchMap.get(id);
            if (tm == null) {
                unmatch++;
                continue;
            }
            if (tm.getHome().getLegacyId().equals(tid) && win != null && win > 0) {
                tm.setHomeRacks(1);
                tm.setSetHomeWins(1);
            }
            if (tm.getAway().getLegacyId().equals(tid) && win != null && win > 0 ) {
                tm.setAwayRacks(1);
                tm.setSetAwayWins(1);
            }
            teamMatchMap.put(tm.getLegacyId(), leagueService.save(tm));
        }
        System.out.println("Matches " + results.size() + " unmatched " + unmatch);
        return Boolean.TRUE;
    }

    public void convertPlayerResults() {
        logger.info("Player Results");
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        Validator validator = factory.getValidator();
        int errors = 0;
        Map<Integer, TeamMatch> teamMatchMap = new HashMap<>(1000);
        for (TeamMatch teamMatch : leagueService.findAll(TeamMatch.class)) {
            teamMatchMap.put(teamMatch.getLegacyId(), teamMatch);
        }
        Map<Integer, User> userHashMap = new HashMap<>(1000);
        for (User user : leagueService.findAll(User.class)) {
            userHashMap.put(user.getLegacyId(), user);
        }
        leagueService.deleteAll(PlayerResult.class);
        for(Season s: leagueService.findAll(Season.class)){
            int missed = 0;
            logger.info("Processing " + s.getDisplayName());
            List<Map<String, Object>> homeResults = jdbcTemplate.queryForList(getQuery("home",s));
            List<PlayerResult> playerResults = new ArrayList<>(5000);
            int members = 0;
            for (Map<String, Object> result : homeResults) {
                PlayerResult playerResult = new PlayerResult();
                playerResult.setLegacyId((Integer) result.get("result_id"));
                playerResult.setTeamMatch(teamMatchMap.get((Integer) result.get("match_id")));
                User aUser = userHashMap.get((Integer) result.get("a_player_id"));
                assert aUser != null;
                if (playerResult.getTeamMatch() == null) {
                    //logger.error("Unknown match for " + result.get("match_id"));
                    errors++;
                    continue;
                }
                Team home = playerResult.getTeamMatch().getHome();
                playerResult.setPlayerHome(aUser);
                playerResult.setHomeRacks((Integer) result.get("a_games_won"));
                playerResult.setMatchNumber((Integer) result.get("match_number"));
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNONE" : result.get("hcd_name").toString());
                playerResult.setPlayerHomeHandicap(aHandicap);

                members =  home.getMembers().size();
                if (members > 15) {
                    throw new RuntimeException("Too many people");
                }
                home.addMember(aUser);

                if (members != home.getMembers().size()) {
                    leagueService.save(home);
                }
                playerResults.add(playerResult);
            }

            List<Map<String, Object>> awayResults = jdbcTemplate.queryForList( getQuery("visit",s));
            for (Map<String, Object> result : awayResults) {
                Integer matchNum = (Integer) result.get("match_number");
                Integer matchId = (Integer)  result.get("match_id");

                PlayerResult playerResult = playerResults.stream().parallel()
                        .filter(r -> r.getMatchNumber().equals(matchNum))
                        .filter(r -> r.getTeamMatch().getLegacyId().equals(matchId)).findFirst().orElse(null);
                if (playerResult == null) {
                    errors++;
                    continue;
                }
                User aUser = userHashMap.get((Integer) result.get("a_player_id"));
                assert aUser != null;
                Team away = playerResult.getTeamMatch().getAway();
                playerResult.setPlayerAway(aUser);
                playerResult.setAwayRacks((Integer) result.get("a_games_won"));
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNONE" : result.get("hcd_name").toString());
                playerResult.setPlayerAwayHandicap(aHandicap);

                members =  away.getMembers().size();
                if (members > 15) {
                    throw new RuntimeException("Too many people");
                }
                away.addMember(aUser);
                if (members != away.getMembers().size()) {
                    leagueService.save(away);
                }
            }

            User forfetUser = leagueService.findAll(User.class).stream().filter(u -> u.getLastName().equals("FORFEIT")).findFirst().get();
            System.out.println("Processing PlayerResults: " + playerResults.size());
            playerResultRepository.save(new Iterable<PlayerResult>() {
                @Override
                public Iterator<PlayerResult> iterator() {
                    return playerResults.iterator();
                }
            });


            /*
                if (r.getPlayerAway() == null) {
                    r.setPlayerAway(forfetUser);
                    r.setPlayerAwayHandicap(Handicap.UNKNOWN);
                }
                if (r.getPlayerHome() == null) {
                    r.setPlayerHome(forfetUser);
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
                */
        }
        logger.warn("Errors : " + errors);
        cacheUtil.refreshAllCache();
        }
        //


    public void userHandicap() {
        logger.info("User Handicaps");
        List<PlayerResult> playerResults = leagueService.findAll(PlayerResult.class);
        logger.info("Got player results");
        List<User> users =  leagueService.findAll(User.class);
        List<Season> seasons = leagueService.findAll(Season.class);
        for (User user : users) {
            user.getHandicapSeasons().clear();
            List<PlayerResult> userResults = playerResults.stream().filter(r->r.hasUser(user)).collect(Collectors.toList());
            for (Season season : seasons) {
                PlayerResult result = userResults.stream().filter(r->r.getSeason().equals(season)).max(new Comparator<PlayerResult>() {
                    @Override
                    public int compare(PlayerResult playerResult, PlayerResult t1) {
                        return t1.getMatchDate().compareTo(playerResult.getMatchDate());
                    }
                }).orElse(null);

                if (result == null) {
                    continue;
                }
                HandicapSeason hs = new HandicapSeason(result.getHandicap(user),season);
                user.addHandicap(hs);
            }
            leagueService.save(user);
        }
    }


    private String getQuery(String type, Season s) {
        return String.format("select  m.season_id,\n" +
                "a.result_id,m.season_id,a.match_id,\n" +
                "a.team_id as a_team_id,a.player_id as a_player_id,a.player_handicap as a_player_handicap,a.games_won as a_games_won " +
                ",ahc.hcd_name, a.match_number as match_number\n" +
                "from result_ind a join match_schedule m on m.match_id = a.match_id\n" +
                "and m.%s_team_id = a.team_id\n" +
                "left JOIN handicap_display ahc ON ahc.hcd_id=a.player_handicap\n" +
                "where a.player_id not in (218,224,905) " +
                " and  season_id = %s " +
                "order by match_id \n" +
                ";\n", type, s.getLegacyId() + "");
    }

    public void convertChallengers() {
        List<PlayerResult> remove = leagueService.findAll(PlayerResult.class)
                .stream().filter(r -> r.getSeason() != null).filter(r -> r.getSeason().isChallenge()).collect(Collectors.toList()
                );
        for (PlayerResult result : remove) {
            leagueService.delete(result);
        }
        leagueService.deleteAll(Challenge.class);
        leagueService.deleteAll(Slot.class);

        for(Team team: leagueService.findAll(Team.class).stream().parallel().filter(t->t.getSeason().isChallenge()).collect(Collectors.toList())) {
            leagueService.delete(team);
        }
        for (TeamMatch teamMatch : leagueService.findAll(TeamMatch.class).stream().parallel().filter(tm->tm.getSeason().getDivision().isChallenge()).collect(Collectors.toList())) {
            leagueService.delete(teamMatch);
        }

        Season challenge = leagueService.findAll(Season.class).stream().filter(s->s.getDivision() == Division.NINE_BALL_CHALLENGE).findFirst().orElse(null);
        if (challenge == null){
            challenge = new Season();
            challenge.setType("Challenge");
            challenge.setSeasonStatus(Status.ACTIVE);
            challenge.setDivision(Division.NINE_BALL_CHALLENGE);
            challenge.setStartDate(LocalDateTime.now().minusDays(90));
            challenge.setLegacyId(4003);
            challenge = leagueService.save(challenge);
        }

        System.err.println("Creating new challenge user");
        List<Map<String,Object>> challengeUsers =
                jdbcTemplate.queryForList("select distinct u.* from leagues_dev.users u " +
                        "join leagues_dev.player p on u.user_id = p.user_id where p.season_id =  4003; ");
        List<User> users = leagueService.findAll(User.class);
        for (Map<String, Object> result : challengeUsers) {
            Integer id = (Integer) result.get("user_id");
            User u = users.stream().parallel().filter(user->user.getLegacyId().equals(id)).findFirst().orElse(null);
            if (u != null) {
                challengeService.createChallengeUser(u);
                continue;
            }
            u = new User();
            u.setLogin((String) result.get("login"));
            u.setEmail((String) result.get("login"));
            u.setFirstName((String) result.get("first_name"));
            u.setLastName((String) result.get("last_name"));
            u.setPassword((String) result.get("password"));
            u.setRole(Role.PLAYER);
            u.setStatus(Status.ACTIVE);
            u.setLegacyId(id);
            u = leagueService.save(u);
            challengeService.createChallengeUser(u);
        }
        System.err.println("Creating new chllange results");
        List<Map<String,Object>> player_results = jdbcTemplate.queryForList("select p.*,m.match_date" +
                " from leagues_dev.player_result p join leagues_dev.team_match m on p.team_match_id=m.team_match_id");
        List<Map<String,Object>> players  = jdbcTemplate.queryForList("select * from leagues_dev.player");
        users = leagueService.findAll(User.class);
        List<TeamMatch> teamMatches = leagueService.findAll(TeamMatch.class);
        List<Team> teams = leagueService.findAll(Team.class).stream().parallel().filter(t->t.isChallenge()).collect(Collectors.toList());
        List<PlayerResult> challengePlayerResults = leagueService.findAll(PlayerResult.class).stream().parallel()
                .filter(pr -> pr.getSeason().getDivision().isChallenge()).collect(Collectors.toList());

        for (Map<String, Object> player_result : player_results) {
            Map<String, Object> home = players.stream().parallel().
                    filter(p -> p.get("player_id").equals(player_result.get("player_home_id"))).findFirst().orElse(null);
            Map<String, Object> away = players.stream().parallel().
                    filter(p -> p.get("player_id").equals(player_result.get("player_away_id"))).findFirst().orElse(null);
            if (home == null || away == null) {
                throw new RuntimeException("cannot find player " + player_result.get("player_result_id"));
            }
            User userHome = users.stream().filter(u -> u.getLegacyId().equals(home.get("user_id"))).findFirst().orElse(null);
            if (userHome == null) {
                throw new RuntimeException("cannot find home player " + player_result.get("user_id"));
            }
            User userAway = users.stream().filter(u -> u.getLegacyId().equals(away.get("user_id"))).findFirst().orElse(null);
            if (userAway == null) {
                throw new RuntimeException("cannot find away player " + player_result.get("user_id"));
            }
            Team userTeamHome = teams.stream().filter(t->t.getName().equals(userHome.getName())).findFirst().orElse(null);
            if (userTeamHome == null) {
                throw new RuntimeException("user team home is not found "+ userHome.getName());
            }

            Team userTeamAway = teams.stream().filter(t->t.getName().equals(userAway.getName())).findFirst().orElse(null);
            if (userTeamAway == null) {
                throw new RuntimeException("user team home is not found "+ userAway.getName());
            }
            TeamMatch tm = new TeamMatch();
            tm.setHome(userTeamHome);
            tm.setAway(userTeamAway);
            Timestamp ts = (Timestamp) player_result.get("match_date");
            tm.setMatchDate(ts.toLocalDateTime());
            tm.setLegacyId((Integer) player_result.get("team_match_id"));
            tm.setAwayRacks((Integer) player_result.get("away_racks"));
            tm.setHomeRacks((Integer) player_result.get("home_racks"));
            tm = leagueService.save(tm);

            PlayerResult pr = new PlayerResult();
            pr.setTeamMatch(tm);
            pr.setMatchNumber(0);
            pr.setPlayerHome(userHome);
            pr.setPlayerAway(userAway);
            pr.setLegacyId((Integer) player_result.get("player_result_id"));
            Handicap homeHandicap = Handicap.values()[(Integer) home.get("handicap")];
            Handicap awayHandicap = Handicap.values()[(Integer) away.get("handicap")];
            pr.setPlayerHomeHandicap(homeHandicap);
            pr.setPlayerAwayHandicap(awayHandicap);
            pr.setAwayRacks((Integer) player_result.get("away_racks"));
            pr.setHomeRacks((Integer) player_result.get("home_racks"));
            resultService.createOrModify(pr);
        }

        List<PlayerResult> challengeResults = leagueService.findAll(PlayerResult.class).stream().parallel().filter(pr->pr.getSeason().getDivision().isChallenge()).collect(Collectors.toList());
        users = users.stream().filter(u->u.isChallenge()).collect(Collectors.toList());
        for (User user : users) {
            PlayerResult result = challengeResults.stream().filter(c->c.hasUser(user)).max(new Comparator<PlayerResult>() {
                @Override
                public int compare(PlayerResult playerResult, PlayerResult t1) {
                    //return t1.getMatchDate().compareTo(playerResult.getMatchDate());
                    return playerResult.getMatchDate().compareTo(t1.getMatchDate());
                }
            }).orElse(null);
            if (result == null){
                continue;
            }
            HandicapSeason hs = user.getHandicapSeasons().stream().filter(h->h.getSeason().equals(result.getSeason())).findFirst().orElse(null);
            if (hs == null){
                throw  new RuntimeException("asdsad");
            }

            hs.setHandicap(result.getHandicap(user));
            leagueService.save(user);
        }
        List<Map<String,Object>> oldChallenges = jdbcTemplate.queryForList("select * from leagues_dev.challenge " +
                "c join leagues_dev.slot s on c.slot_id=s.slot_id " +
                "where s.slot_time > now() and status = 'ACCEPTED' or status = 'PENDING' order by player_challenger_id,player_opponent_id,slot_time; ");

        Map<Integer,Slot>  slots = new HashMap<>();
        for(Slot s: leagueService.findAll(Slot.class)) {
            slots.put(s.getLegacyId(), s);
        }

        for (Map<String, Object> ch : oldChallenges) {
            Status status = Status.valueOf(ch.get("status").toString());
               Map<String, Object> home = players.stream().parallel().
                    filter(p -> p.get("player_id").equals(ch.get("player_challenger_id"))).findFirst().orElse(null);
            Map<String, Object> away = players.stream().parallel().
                    filter(p -> p.get("player_id").equals(ch.get("player_opponent_id"))).findFirst().orElse(null);
            if (home == null || away == null) {
                throw new RuntimeException("cannot find player " + ch.get("challenger_id"));
            }
            User userHome = users.stream().filter(u -> u.getLegacyId().equals(home.get("user_id"))).findFirst().orElse(null);
            if (userHome == null) {
                throw new RuntimeException("cannot find home player " + ch.get("user_id"));
            }
            User userAway = users.stream().filter(u -> u.getLegacyId().equals(away.get("user_id"))).findFirst().orElse(null);
            if (userAway == null) {
                throw new RuntimeException("cannot find away player " + ch.get("user_id"));
            }

            Team userTeamHome = teams.stream().filter(t->t.getName().equals(userHome.getName())).findFirst().orElse(null);
            if (userTeamHome == null) {
                throw new RuntimeException("user team home is not found "+ userHome.getName());
            }

            Team userTeamAway = teams.stream().filter(t->t.getName().equals(userAway.getName())).findFirst().orElse(null);
            if (userTeamAway == null) {
                throw new RuntimeException("user team home is not found "+ userAway.getName());
            }

            if (status == Status.ACCEPTED) {
                Slot s = slots.get((Integer) ch.get("slot_id"));
                if (s == null) {
                    s = new Slot();
                    s.setLegacyId((Integer) ch.get("slot_id"));
                    s.setAllocated(0);
                    Timestamp ts = (Timestamp) ch.get("slot_time");
                    s.setTime(ts.toLocalDateTime());
                    s = leagueService.save(s);
                    slots.put(s.getLegacyId(), s);
                }
                Challenge newChallenge = new Challenge();
                newChallenge.setStatus(status);
                newChallenge.setAcceptedSlot(s);
                newChallenge.setLegacyId((Integer) ch.get("challenge_id"));
                newChallenge.setChallenger(userTeamHome);
                newChallenge.setOpponent(userTeamAway);
                newChallenge.setSlots(Arrays.asList(s));
                leagueService.save(newChallenge);
                continue;
            }
        }
    }

    public void stats() {
        User u = leagueService.findByLogin("doug.rhee@societybilliards.com");
        logger.info("\n\n" + u.getHandicapSeasons());
        logger.info("Teams: " + leagueService.findAll(Team.class).stream().parallel().filter(t->t.hasUser(u)).count());
        logger.info("Results: " + leagueService.findAll(PlayerResult.class).stream().parallel().filter(t -> t.hasUser(u)).count());
    }
}
