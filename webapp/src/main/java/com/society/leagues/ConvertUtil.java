package com.society.leagues;


import com.society.leagues.service.ChallengeService;
import com.society.leagues.service.LeagueService;
import com.society.leagues.service.ResultService;
import com.society.leagues.cache.CacheUtil;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.PlayerResultRepository;
import com.society.leagues.service.TeamService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


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

    @Autowired ChallengeService challengeService;
    @Autowired LeagueService leagueService;
    @Autowired ResultService resultService;
    @Autowired PlayerResultRepository playerResultRepository;
    @Autowired CacheUtil cacheUtil;
    @Autowired TeamService teamService;
    @Autowired JdbcTemplate jdbcTemplate;

    String defaultPassword = new BCryptPasswordEncoder().encode("abc123");
    public void convertUser() {
        logger.info("Convert users");
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
    }

    public List<Map<String,Object>> handicaps() {
        return jdbcTemplate.queryForList("select * from handicap_display");
    }

    public Boolean convertSeason() {
        logger.info("Convert season");
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
                        " where match_start_date > '0000-00-00 00:00:00' and m.season_id > 1 and l.league_id != 4 " +
                        " group by m.season_id,m.league_id;");

        for (Map<String, Object> result : results) {
            Season s = new Season();
            String name = (String) result.get("name");
            s.setLegacyId((Integer) result.get("season_id"));
            String ts = (String) result.get("start_date");
            if (ts != null)
                s.setStartDate(LocalDateTime.parse(ts.replace(" ","T")));
            else
                s.setStartDate(LocalDateTime.now().minusDays(100));


            if (name.contains("2015,Fall")) {
                s.setEndDate(LocalDateTime.now().plusDays(60));
                s.setSeasonStatus(Status.ACTIVE);
            } else {
                s.setEndDate(LocalDateTime.now().minusDays(60));
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
            if (name.contains("9-ball")) {
                s.setDivision(Division.NINE_BALL_TUESDAYS);
            }
            if (name.contains("Straight")) {
                s.setDivision(Division.STRAIGHT);
            }
            if (name.toLowerCase().contains("fall")) {
                s.setType("Fall");
            }
            if (name.toLowerCase().contains("summer")) {
                s.setType("Summer");
            }
            if (name.toLowerCase().contains("winter")) {
                s.setType("Winter");
            }
            if (name.toLowerCase().contains("spring")) {
                s.setType("Spring");
            }
            s = leagueService.save(s);
        }
        if (leagueService.findAll(Season.class).size() > (results.size()+1)) {
            throw new RuntimeException("season");
        }
        leagueService.findAll(Season.class).stream().sorted(new Comparator<Season>() {
            @Override
            public int compare(Season o1, Season o2) {
                return o1.getStartDate().compareTo(o2.getStartDate());
            }
        }).forEach(s -> System.out.println(s.toString()));
        return true;
    }

    public void convertTeam() {
        logger.info("Teams ");
                logger.info("Deleted Teams");
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select distinct season_id,name,team_id " +
                "from ( select distinct season_id,t.name,team_id  " +
                "from match_schedule s join team t  on s.home_team_id=t.team_id where s.league_id != 4  " +
                "union " +
                "select distinct season_id,t.name,team_id from match_schedule s join" +
                " team t on s.visit_team_id=t.team_id where s.league_id != 4) " +
                "as teams " +
                "where season_id > 1  order by season_id,team_id");
        List<Season> seasons = leagueService.findAll(Season.class);
        for (Map<String, Object> result : results) {
            Integer id = (Integer) result.get("season_id");
            Season s = seasons.stream().filter(sn->sn.getLegacyId().equals(id)).findFirst().orElse(null);
            if (s == null) {
                throw new RuntimeException("Could not find season " + id);
            }
            Team t = teamService.createTeam(result.get("name").toString(), s);
            t.setLegacyId((Integer) result.get("team_id"));
            leagueService.save(t);
        }

        System.out.println("Created " + leagueService.findAll(Team.class).size() + " teams");
    }

    public void convertTeamMembers() {
       List<Map<String,Object>> results  = jdbcTemplate.queryForList(
               "select distinct player_id,team_id,season_id  " +
                       "from result_ind r join match_schedule m on r.match_id = m.match_id  " +
                       "where player_id is not null and team_id is not null and team_id > 0 and player_id > 0 and  m.league_id != 4" +
                       " order by team_id,season_id"
       );
        List<User> users = leagueService.findAll(User.class);
        List<Team> teams = leagueService.findAll(Team.class);
        Map<Integer,User> userMap = new HashMap<>();
        for (User user : users) {
            userMap.put(user.getLegacyId(),user);
        }
        for (Map<String, Object> result : results) {
            Integer id = (Integer) result.get("player_id");
            User u = userMap.get(id);
            Team team = teams.parallelStream()
                    .filter(t -> t.getLegacyId().equals(result.get("team_id")))
                    .filter(t -> t.getSeason().getLegacyId().equals(result.get("season_id"))).findFirst().orElse(null);

            if (team == null) {
                logger.error("Could not find team " + result.get("team_id"));
                continue;
            }
            team.getMembers().addMember(u);
        //    leagueService.save(team.getMembers());
        }
        teams.forEach(t->leagueService.save(t.getMembers()));
        System.out.print("Found  " + results.size() + " teams with members ");
    }

    public Boolean converTeamMatch() {
        logger.info("Convert team match");
                List<Map<String,Object>> matches = jdbcTemplate.
                queryForList("select match_number,match_id,m.season_id,home_team_id,visit_team_id,match_start_date " +
                        "from match_schedule m " +
                        "where match_start_date is not null " +
                        " and match_start_date > 2000-01-01 and m.season_id > 0  and m.league_id != 4" +
                        " and  (home_team_id is not null and visit_team_id is not null) order by match_id");

        List<Team> teams  = leagueService.findAll(Team.class);
        int unmatched = 0;
        for (Map<String, Object> match : matches) {
            Integer mid = (Integer) match.get("match_id");
            TeamMatch tm = new TeamMatch();
            tm.setMatchNumber((Integer) match.get("match_number"));
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
                //tm.setSetHomeWins(1);
            }
            if (tm.getAway().getLegacyId().equals(tid) && win != null && win > 0 ) {
                tm.setAwayRacks(1);
                //tm.setSetAwayWins(1);
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
        List<Map<String,Object>> objects = new ArrayList<>();
        List<Map<String,Object>> handicaps = handicaps();
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
                User aUser = userHashMap.get((Integer) result.get("player_id"));
                assert aUser != null;
                if (playerResult.getTeamMatch() == null) {
                    //logger.error("Unknown match for " + result.get("match_id"));
                    errors++;
                    continue;
                }
                Team home = playerResult.getTeamMatch().getHome();
                playerResult.setPlayerHome(aUser);
                playerResult.setHomeRacks((Integer) result.get("games_won"));
                playerResult.setMatchNumber((Integer) result.get("match_number"));
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNOWN" : result.get("hcd_name").toString());
                Integer leagueId = (Integer) result.get("league_id");
                Integer handicapId = (Integer) result.get("player_handicap");
                if (!leagueId.equals(1) && aHandicap == Handicap.UNKNOWN) {
                    aHandicap = Handicap.get(handicapId.toString());
                }
                playerResult.setPlayerHomeHandicap(aHandicap);

                //members =  home.getMembers().size();
                //if (members > 15) {
//                    throw new RuntimeException("Too many people");
  //              }
    //            home.addMember(aUser);

      //          if (members != home.getMembers().size()) {
                    //leagueService.save(home.getMembers());
          //          leagueService.save(home);
        //        }
                playerResults.add(playerResult);
            }

            List<Map<String, Object>> awayResults = jdbcTemplate.queryForList(getQuery("visit", s));
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
                User aUser = userHashMap.get((Integer) result.get("player_id"));
                assert aUser != null;
                Team away = playerResult.getTeamMatch().getAway();
                playerResult.setPlayerAway(aUser);
                playerResult.setAwayRacks((Integer) result.get("games_won"));
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNOWN" : result.get("hcd_name").toString());
                 Integer leagueId = (Integer) result.get("league_id");
                Integer handicapId = (Integer) result.get("player_handicap");
                if (!leagueId.equals(1) && aHandicap == Handicap.UNKNOWN) {
                    aHandicap = Handicap.get(handicapId.toString());
                }
                if (aHandicap == Handicap.UNKNOWN) {
                    //logger.info("Looking up handicap for " + playerResult.getLegacyId());
                  //   aHandicap = Handicap.get(handicapId.toString());
                    /*
                    Map<String, Object> possibleResult = handicaps.stream().filter(h -> h.get("hcd_handicap").equals(handicapId)).findFirst().orElse(null);
                    if (possibleResult != null) {
                        aHandicap = Handicap.get(possibleResult.get("hcd_name") == null ? "UNKNOWN" : possibleResult.get("hcd_name").toString());
                        if (aHandicap == Handicap.UNKNOWN)
                            logger.info("Still no handicap");
                    }
                    */
                }

                playerResult.setPlayerAwayHandicap(aHandicap);

              /*  members =  away.getMembers().size();
                if (members > 15) {
                    throw new RuntimeException("Too many people");
                }
                away.addMember(aUser);
                if (members != away.getMembers().size()) {
                    leagueService.save(away.getMembers());
                    leagueService.save(away);
                }
                */
            }

            User forfetUser = leagueService.findAll(User.class).stream().filter(u -> u.getLastName().equals("FORFEIT")).findFirst().get();
            System.out.println("Processing PlayerResults: " + playerResults.size());
            playerResultRepository.save(new Iterable<PlayerResult>() {
                @Override
                public Iterator<PlayerResult> iterator() {
                    return playerResults.iterator();
                }
            });
        }
        cacheUtil.refreshAllCache();
        logger.warn("Errors : " + errors);
        }

    public void userHandicap() {
        logger.info("User Handicaps");
        List<PlayerResult> playerResults = leagueService.findAll(PlayerResult.class);
        logger.info("Got player results");
        List<User> users =  leagueService.findAll(User.class);
        List<Season> seasons = leagueService.findAll(Season.class);
        for (final User user : users) {
            //user.getHandicapSeasons().clear();
            List<PlayerResult> userResults = playerResults.stream().filter(r->r.hasUser(user)).collect(Collectors.toList());
            for (Season season : seasons) {
                PlayerResult result = userResults.stream()
                        .filter(r->r.getSeason().equals(season))
                        .max(new Comparator<PlayerResult>() {
                    @Override
                    public int compare(PlayerResult playerResult, PlayerResult t1) {
                        return playerResult.getMatchDate().compareTo(t1.getMatchDate());
                    }
                }).orElse(null);

                if (result == null) {
                    continue;
                }
                HandicapSeason hs = user.getHandicapSeasons().stream()
                        .filter(h->h.getSeason().equals(season)).findFirst()
                        .orElse(new HandicapSeason(result.getHandicap(user),season));
                hs.setHandicap(result.getHandicap(user));
                if (season.isNine() && !Handicap.isNine(hs.getHandicap())) {
                    logger.info("Wrong handicap " + season.getDisplayName());
                }
                user.addHandicap(hs);
            }
            leagueService.save(user);
        }
    }

    public void teamMembers() {
        List<Map<String,Object>> players =
                jdbcTemplate.queryForList("select t.*,d.*,tp_player " +
                        " from team_player tp join division d on tp_division=d.division_id  " +
                        " join team t on tp_team=t.team_id where tp_division " +
                        " in (select distinct ad_division from active_divisions) " +
                        " and tp_team is not null and d.league_id != 4 ");
        Map<Integer, Set<Team>> teamMap = leagueService.findAll(Team.class).stream()
                .filter(t -> t.getSeason().isActive()).filter(t -> t.getLegacyId() != null)
                .collect(Collectors.groupingBy(LeagueObject::getLegacyId, Collectors.toSet()));

        Map<Integer, Set<User>>
                userHashMap = leagueService.findAll(User.class)
                .stream().collect(Collectors.groupingBy(LeagueObject::getLegacyId, Collectors.toSet()));

        for (Map<String, Object> player : players) {
            if (player.get("team_id") == null || teamMap.get((Integer) player.get("team_id")) == null || teamMap.get((Integer) player.get("team_id")).isEmpty()) {
                logger.error("no team  found for " + player.get("team_id"));
                continue;
            }
            Team t = teamMap.get((Integer) player.get("team_id")).iterator().next();
            User u = userHashMap.get((Integer) player.get("tp_player")).iterator().next();
            Season s = leagueService.findAll(Season.class).stream().parallel().filter(se -> se.getLegacyId().equals(
                    (Integer) player.get("season_id")
            )).findFirst().get();
            HandicapSeason hs = new HandicapSeason();
            hs.setSeason(s);
            if (u.getHandicapSeasons().stream().filter(h->h.getSeason().equals(s)).count() >0) {
                hs = u.getHandicapSeasons().stream().filter(h->h.getSeason().equals(s)).findFirst().get();
            }
            if (hs.getHandicap() == null) {
                for (HandicapSeason handicapSeason : u.getHandicapSeasons()) {
                    if (handicapSeason.getSeason().getYear().equals("2015")
                            && handicapSeason.getSeason().getType().equals("Summer")
                            && s.getDivision() == handicapSeason.getSeason().getDivision()
                            ) {
                        hs.setHandicap(handicapSeason.getHandicap());
                    }
                }
                if (hs.getHandicap() == null) {
                    hs.setHandicap(hs.getSeason().isNine() ? Handicap.DPLUS : Handicap.FOUR);
                }
                u.addHandicap(hs);
                leagueService.save(u);
            }
            t.getMembers().addMember(u);
            leagueService.save(t.getMembers());
        }
    }

    private String getQuery(String type, Season s) {
        return String.format("select  m.league_id, m.season_id,\n" +
                "a.result_id,m.season_id,a.match_id,\n" +
                "a.team_id as team_id,a.player_id as player_id," +
                "a.player_handicap as player_handicap," +
                "a.games_won as games_won " +
                ",ahc.*, a.match_number as match_number\n" +
                "from result_ind a " +
                "join match_schedule m on m.match_id = a.match_id\n" +
                "and m.%s_team_id = a.team_id\n" +
                "left JOIN handicap_display ahc ON ahc.hcd_id=a.player_handicap and ahc.hcd_league=m.league_id \n" +
                "where a.player_id not in (218,224,905) and player_id > 0 " +
                " and  season_id = %s  and m.league_id != 4 " +
                "order by match_id \n" +
                ";\n", type, s.getLegacyId() + "");
    }


     private String getScrambleQuery(String type, Season s) {
        return String.format("select  a.scotch , m.season_id,\n" +
                "a.result_id,m.season_id,a.match_id,\n" +
                "a.team_id as team_id,a.player_id as player_id," +
                "a.player_handicap as player_handicap,a.games_won as games_won " +
                ",ahc.hcd_name, m.league_id,a.match_number as match_number\n" +
                "from result_ind a " +
                "join match_schedule m on m.match_id = a.match_id\n" +
                "and m.%s_team_id = a.team_id\n" +
                "left JOIN handicap_display ahc ON ahc.hcd_id=a.player_handicap and ahc.hcd_league=m.league_id \n" +
                "where a.player_id not in (218,224,905) and player_id > 0  " +
                " and  season_id = %s  " +
                "order by match_id \n" +
                ";\n", type, s.getLegacyId() + "");
    }

    public void convertChallengers() {
        List<PlayerResult> remove = leagueService.findAll(PlayerResult.class)
                .stream().filter(r -> r.getSeason() != null).filter(r -> r.getSeason().isChallenge()).collect(Collectors.toList()
                );
        for (PlayerResult result : remove) {
            leagueService.purge(result);
        }
        leagueService.deleteAll(Challenge.class);
        leagueService.deleteAll(Slot.class);

        for(Team team: leagueService.findAll(Team.class).stream().parallel().filter(t->t.getSeason().isChallenge()).collect(Collectors.toList())) {
            leagueService.purge(team);
        }
        for (TeamMatch teamMatch : leagueService.findAll(TeamMatch.class).stream().parallel().filter(tm->tm.getSeason().getDivision().isChallenge()).collect(Collectors.toList())) {
            leagueService.purge(teamMatch);
        }

        Season challenge = leagueService.findAll(Season.class).stream().filter(s->s.getDivision() == Division.NINE_BALL_CHALLENGE).findFirst().orElse(null);
        if (challenge == null){
            challenge = new Season();
            challenge.setType("Challenge");
            challenge.setSeasonStatus(Status.ACTIVE);
            challenge.setDivision(Division.NINE_BALL_CHALLENGE);
            challenge.setStartDate(LocalDateTime.now().minusDays(100));
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
                .filter(pr -> pr.getSeason().isChallenge()).collect(Collectors.toList());

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

        List<PlayerResult> challengeResults = leagueService.findAll(PlayerResult.class).stream().parallel()
                .filter(pr -> pr.getSeason().getDivision().isChallenge())
                .collect(Collectors.toList());
        users = users.stream().filter(u->u.isChallenge()).collect(Collectors.toList());//.stream().filter(u->u.getLastName().equals("Chimento")).collect(Collectors.toList());

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
                    s.setTimeStamp(ts.toLocalDateTime());
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
        User u = leagueService.findByLogin("dchimento@gmail.com");
        List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().parallel().filter(t -> t.hasUser(u)).collect(Collectors.toList());
        List<Team> teams = leagueService.findAll(Team.class).stream().parallel().filter(t->t.hasUser(u)).collect(Collectors.toList());
        logger.info("\n\n" + u.getHandicapSeasons());
        logger.info("Teams: " + teams.size());
        logger.info("Results: " + results.size());
        List<Season> teamSeason = new ArrayList<>();
        List<Season> userSeason = u.getSeasons();
        teams.stream().forEach(t -> teamSeason.add(t.getSeason()));
        for (Season season : u.getSeasons()) {
            if (!teamSeason.contains(season)) {
                logger.info(season + " is not in the team list");
            }
        }
        for (Season season : teamSeason) {
            if (!userSeason.contains(season))
                logger.info(season + " is not in the user season list");
        }
    }

    public void convertScramble() {
        Season eight = new Season();
        eight.setLegacyId(74);
        eight.setStartDate(LocalDate.now().withYear(2015).withDayOfMonth(15).withMonth(8).atStartOfDay());
        eight.setDivision(Division.MIXED_MONDAYS_MIXED);
        eight.setYear("2015");
        eight.setType("Fall");
        eight.setSeasonStatus(Status.ACTIVE);
        eight = leagueService.save(eight);

        List<Map<String, Object>> results = jdbcTemplate.queryForList(
                "select distinct season_id,name,team_id " +
                        "from ( select distinct season_id,t.name,team_id " +
                        "from match_schedule s join team t " +
                        " on s.home_team_id=t.team_id" +
                        " where s.season_id = 74  " +
                        "union " +
                        "select distinct season_id,t.name,team_id " +
                        "from match_schedule s join" +
                        " team t on s.visit_team_id=t.team_id" +
                        " where s.season_id = 74) " +
                        "as teams " +
                        "where season_id = 74 order by season_id,team_id");


        for (Map<String, Object> result : results) {
            Integer id = (Integer) result.get("season_id");
            Team t = teamService.createTeam(result.get("name").toString(), eight);
            t.setLegacyId((Integer) result.get("team_id"));
            leagueService.save(t);
        }

         Map<Integer, Set<User>>
                userHashMap = leagueService.findAll(User.class)
                .stream().collect(Collectors.groupingBy(LeagueObject::getLegacyId, Collectors.toSet()));
        results = jdbcTemplate.queryForList("select player_id,hcd_handicap from player p join handicap_display  hc on hc_m8=hc.hcd_id;");
        for (Map<String, Object> result : results) {
            User u = userHashMap.get((Integer) result.get("player_id")).stream().findFirst().get();
            HandicapSeason hs = new HandicapSeason();
            hs.setSeason(eight);
            hs.setHandicap(Handicap.get(result.get("hcd_handicap").toString()));
            u.addHandicap(hs);
            leagueService.save(u);
        }

        logger.info("Convert team match");
        List<Map<String, Object>> matches = jdbcTemplate.
                queryForList("select match_id,m.season_id,home_team_id,visit_team_id,match_start_date " +
                        "from match_schedule m " +
                        "where match_start_date is not null " +
                        " and match_start_date > 2000-01-01   and m.season_id = 74" +
                        " and  (home_team_id is not null and visit_team_id is not null) order by match_id");

        List<Team> teams = leagueService.findAll(Team.class).stream().filter(team -> team.getSeason().isScramble()).collect(Collectors.toList());
        int unmatched = 0;
        for (Map<String, Object> match : matches) {
            Integer mid = (Integer) match.get("match_id");
            TeamMatch tm = new TeamMatch();
            tm.setLegacyId(mid);
            Team home = teams.stream().filter(t ->
                    t.getLegacyId().equals(match.get("home_team_id"))).
                    findFirst().orElse(null);

            if (home == null) {
                unmatched++;
                continue;
            }

            Team away = teams.stream().filter(t ->
                    t.getLegacyId().equals(match.get("visit_team_id"))).
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

        /************************/

        logger.info("TeamMatch Results");
        results = jdbcTemplate.queryForList(" select * from result_team");
        List<TeamMatch> teamMatchList = leagueService.findAll(TeamMatch.class)
                .stream()
                .filter(tm -> tm.getSeason().isScramble())
                .collect(Collectors.toList());

        Map<Integer, TeamMatch> teamMatchMap = new HashMap<>();
        for (TeamMatch teamMatch : teamMatchList) {
            teamMatchMap.put(teamMatch.getLegacyId(), teamMatch);
        }
        int unmatch = 0;
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
                ///tm.setSetHomeWins(1);
            }
            if (tm.getAway().getLegacyId().equals(tid) && win != null && win > 0) {
                tm.setAwayRacks(1);
                //tm.setSetAwayWins(1);
            }
            teamMatchMap.put(tm.getLegacyId(), leagueService.save(tm));
        }
        System.out.println("Matches " + results.size() + " unmatched " + unmatch);

        }

    public void scrambleResults() {

        logger.info("Scrmable Results");


        int errors = 0;
        HashMap<Integer,TeamMatch> teamMatchMap = new HashMap<>(1000);
        for (TeamMatch teamMatch : leagueService.findAll(TeamMatch.class)) {
            teamMatchMap.put(teamMatch.getLegacyId(), teamMatch);
        }

        Season s = leagueService.findAll(Season.class).stream().filter(Season::isScramble).findFirst().get();
        int missed = 0;
        logger.info("Processing " + s.getDisplayName());
        List<Map<String, Object>> homeResults = jdbcTemplate.queryForList(getScrambleQuery("home", s));
        Map<Integer, Set<User>>
                userHashMap = leagueService.findAll(User.class)
                .stream().collect(Collectors.groupingBy(LeagueObject::getLegacyId, Collectors.toSet()));

        List<PlayerResult> playerResults = new ArrayList<>(5000);

        for (Map<String, Object> result : homeResults) {
            Integer id = (Integer) result.get("result_id");
            Integer matchId = (Integer) result.get("match_id");
            Integer matchNumber = (Integer) result.get("match_number");
            Boolean scotch = ((Integer) result.get("scotch")) > 0;
            TeamMatch teamMatch = leagueService.findAll(TeamMatch.class).parallelStream().filter(tm->tm.getLegacyId().equals(matchId)).findFirst().get();
            PlayerResult playerResult = null;
            PlayerResult partnerMatch = null;
            playerResult = leagueService.findAll(PlayerResult.class).stream().filter(
                    pr -> pr.getTeamMatch().getLegacyId().equals(matchId)
            ).filter(
                    pr->pr.getMatchNumber().equals(matchNumber)
            )
                    .findFirst().orElse(null);

            if (playerResult == null) {
                playerResult = new PlayerResult();
                playerResult.setLegacyId(id);
                playerResult.setTeamMatch(teamMatch);
                playerResult.setMatchNumber(matchNumber);
                playerResult.setPlayerHome(userHashMap.get((Integer) result.get("player_id")).stream().findFirst().get());
                playerResult.setScotch(scotch);
                playerResult.setHomeRacks((Integer) result.get("games_won"));
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNOWN" : result.get("hcd_name").toString());
                playerResult.setPlayerHomeHandicap(aHandicap);
                leagueService.save(playerResult);
            } else {
                //Partner
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNOWN" : result.get("hcd_name").toString());
                playerResult.setPlayerHomeHandicapPartner(aHandicap);
                playerResult.setPlayerHomePartner(userHashMap.get((Integer) result.get("player_id")).stream().findFirst().get());
                leagueService.save(playerResult);
            }
        }

        List<Map<String, Object>> awayResults = jdbcTemplate.queryForList(getScrambleQuery("visit", s));
        for (Map<String, Object> result : awayResults) {
              Integer id = (Integer) result.get("result_id");
            Integer matchId = (Integer) result.get("match_id");
            Integer matchNumber = (Integer) result.get("match_number");
            Boolean scotch = ((Integer) result.get("scotch")) > 0;
            TeamMatch teamMatch = leagueService.findAll(TeamMatch.class).parallelStream().filter(
                    tm->tm.getLegacyId().equals(matchId))
                    .findFirst().get();
            PlayerResult playerResult = null;
            PlayerResult partnerMatch = null;
            playerResult = leagueService.findAll(PlayerResult.class).stream().filter(
                    pr -> pr.getTeamMatch().getLegacyId().equals(matchId)).filter(
                    pr->pr.getMatchNumber().equals(matchNumber)
            ).findFirst().orElse(null);
            if (playerResult == null) {
                throw new RuntimeException("No match");
            }
            playerResult.setAwayRacks((Integer) result.get("games_won"));
            if (playerResult.getPlayerAway() == null) {
                playerResult.setPlayerAway(userHashMap.get((Integer) result.get("player_id")).stream().findFirst().get());
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNOWN" : result.get("hcd_name").toString());
                playerResult.setPlayerAwayHandicap(aHandicap);
            } else {
                playerResult.setPlayerAwayPartner(userHashMap.get((Integer) result.get("player_id")).stream().findFirst().get());
                Handicap aHandicap = Handicap.get(result.get("hcd_name") == null ? "UNKNOWN" : result.get("hcd_name").toString());
                playerResult.setPlayerAwayHandicapPartner(aHandicap);
            }
            leagueService.save(playerResult);
        }
        cacheUtil.refreshAllCache();
    }

    public void scrambleMembers() {
          List<Map<String,Object>> players =
                jdbcTemplate.queryForList("select t.*,d.*,tp_player " +
                        " from team_player tp join division d on tp_division=d.division_id  " +
                        " join team t on tp_team=t.team_id where tp_division " +
                        " in (select distinct ad_division from active_divisions) " +
                        " and tp_team is not null and d.season_id = 74 ");
        Map<Integer, Set<Team>> teamMap = leagueService.findAll(Team.class).stream()
                .filter(t -> t.getSeason().isActive()).filter(t -> t.getLegacyId() != null)
                .collect(Collectors.groupingBy(LeagueObject::getLegacyId, Collectors.toSet()));

        Map<Integer, Set<User>>
                userHashMap = leagueService.findAll(User.class)
                .stream().collect(Collectors.groupingBy(LeagueObject::getLegacyId, Collectors.toSet()));

        for (Map<String, Object> player : players) {
            if (player.get("team_id") == null || teamMap.get((Integer) player.get("team_id")) == null || teamMap.get((Integer) player.get("team_id")).isEmpty()) {
                logger.error("no team  found for " + player.get("team_id"));
                continue;
            }
            Team t = teamMap.get((Integer) player.get("team_id")).iterator().next();
            User u = userHashMap.get((Integer) player.get("tp_player")).iterator().next();
            t.getMembers().addMember(u);
            leagueService.save(t.getMembers());
        }
    }

    public void clean() {
        List<User> users = leagueService.findAll(User.class);
        for (User user : users) {
            List<PlayerResult> results = leagueService.findAll(PlayerResult.class).parallelStream()
                    .filter(pr -> !pr.getSeason().isActive())
                    .filter(pr -> !pr.getSeason().isChallenge())
                    .filter(pr -> pr.hasUser(user))
                    .collect(Collectors.toList());

            HashSet<Season> seasons = new HashSet<>();
            results.forEach(pr -> seasons.add(pr.getSeason()));
            for (Season season : seasons) {
                if (!user.getSeasons().contains(season)) {
                    HandicapSeason hs = new HandicapSeason(Handicap.UNKNOWN, season);
                    user.addHandicap(hs);
                }
            }

            for (Season season : user.getSeasons().stream()
                    .filter(s -> !s.isChallenge())
                    .filter(s->!s.isActive())
                    .collect(Collectors.toList())
                    ) {
                if (!seasons.contains(season)) {
                    user.removeHandicap(new HandicapSeason(Handicap.UNKNOWN, season));
                }
            }
            leagueService.save(user);
        }
        int cnt = 0;
        for (PlayerResult result : leagueService.findAll(PlayerResult.class)) {
            if (result.getPlayerAwayHandicap() == null || result.getPlayerAwayHandicap() == Handicap.UNKNOWN) {
                cnt++;
                if (cnt % 100 == 0)
                    logger.info("Unknown handicap for " + result);
            }
            if (result.getPlayerHomeHandicap() == null || result.getPlayerHomeHandicap() == Handicap.UNKNOWN) {
                cnt++;
                if (cnt % 100 == 0)
                    logger.info("Unknown handicap for " + result);

            }
        }
    }

    public void captains(){
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select captain_id,team_id from team where captain_id is not null and captain_id > 0;");
        List<Team> teams = leagueService.findAll(Team.class).stream().filter(t -> t.getSeason().isActive()).collect(Collectors.toList());
        List<User>  users = leagueService.findAll(User.class);
        for (Map<String, Object> result : results) {
            Team team = teams.parallelStream().filter(t -> t.getSeason().isActive())
                    .filter(t->t.getLegacyId() != null)
                    .filter(t -> t.getLegacyId().equals(result.get("team_id"))).findFirst().orElse(null);
            if (team == null)
                continue;
            User u = users.parallelStream().filter(user->user.getLegacyId().equals(result.get("captain_id"))).findFirst().orElse(null);
            if (u == null)
                continue;
            team.getMembers().setCaptain(u);
            leagueService.save(team.getMembers());
        }
    }

    public void scrambleClean() {
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select distinct player_id   from result_ind where match_id in (select match_id from match_schedule where season_id = 74)");
        Season season = leagueService.findAll(Season.class).parallelStream().filter(s -> s.getLegacyId().equals(new Integer(74))).findFirst().get();
        List<User> scramble = leagueService.findAll(User.class).parallelStream().filter(u -> u.hasSeason(season)).collect(Collectors.toList());
        for (User user : scramble) {
            boolean found = false;
            for (Map<String, Object> result : results) {
                if (user.getLegacyId().equals(result.get("player_id"))) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                user.removeHandicap(new HandicapSeason(Handicap.UNKNOWN,season));
                leagueService.save(user);
            }
        }
    }

    public void teamMatchRacks() {
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select team_id,r.match_id,sum(games_won) as rw  , sum(games_lost) as rl  from  result_ind r join match_schedule m on r.match_id=m.match_id where m.division_id in (select ad_division from active_divisions) group by team_id,r.match_id");
        List<TeamMatch> matches = leagueService.findAll(TeamMatch.class).parallelStream().filter(tm->tm.getSeason().isActive()).filter(tm->!tm.getSeason().isChallenge()).collect(Collectors.toList());

        for (Map<String, Object> result : results) {
            Integer tid = (Integer) result.get("team_id");
            Integer mid = (Integer) result.get("match_id");
            TeamMatch teamMatch = matches.parallelStream().filter(tm->tm.getLegacyId().equals(mid)).findFirst().get();
            if (teamMatch.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS) {
                continue;
            }
            BigDecimal rw = (BigDecimal) result.get("rw");
            if (teamMatch.getHome().getLegacyId().equals(tid)) {
                teamMatch.setHomeRacks(rw.intValue());
            } else {
                teamMatch.setAwayRacks(rw.intValue());
            }
            leagueService.save(teamMatch);
        }

        List<PlayerResult> nine = leagueService.findCurrent(PlayerResult.class).stream().filter(p -> p.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS).collect(Collectors.toList());
        for (PlayerResult result : nine) {
            TeamMatch tm = result.getTeamMatch();
            User winner = result.getWinner();
            if (tm.getHome().getMembers().getMembers().contains(winner)) {
                tm.setSetHomeWins(tm.getSetHomeWins()+1);
            } else {
                tm.setSetAwayWins(tm.getSetAwayWins()+1);
            }
            leagueService.save(tm);
        }

        Map<TeamMatch,List<PlayerResult>> teamMatchListMap = leagueService.findCurrent(PlayerResult.class).parallelStream().filter(p->p.getSeason().getDivision() == Division.NINE_BALL_TUESDAYS).collect(Collectors.groupingBy(PlayerResult::getTeamMatch));
        for (TeamMatch teamMatch : teamMatchListMap.keySet()) {
            teamMatch.setAwayRacks(0);
            teamMatch.setHomeRacks(0);
            for (PlayerResult match  : teamMatchListMap.get(teamMatch)) {
                Integer  h = match.getHomeRacks();
                Integer  a = match.getAwayRacks();
                teamMatch.setHomeRacks(teamMatch.getHomeRacks()+h);
                teamMatch.setAwayRacks(teamMatch.getAwayRacks()+a);
            }
            leagueService.save(teamMatch);
        }
    }


    public void updateRacks() {
        List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().filter(r->r.getSeason().isScramble()).collect(Collectors.toList());
        for (PlayerResult result : results) {
            if (result.isScotch()) {
                if (result.getHomeRacks()>0) {
                    result.setHomeRacks(2);
                }

                if (result.getAwayRacks()>0) {
                    result.setAwayRacks(2);
                }
            } else {
                if (result.getHomeRacks()>0) {
                    result.setHomeRacks(1);
                }

                if (result.getAwayRacks()>0) {
                    result.setAwayRacks(1);
                }
            }

            playerResultRepository.save(result);
        }

    }

      public void scrambleGameHandicap() {
          List<PlayerResult> results = leagueService.findAll(PlayerResult.class).parallelStream()
                  .filter(p->p.getSeason().isScramble())
                  .filter(PlayerResult::isScotch).
                  collect(Collectors.toList());
          for (PlayerResult result : results) {
              if (result.getPlayerHomeHandicapPartner()  == null && result.getPlayerHomePartner() != null) {
                  result.setPlayerHomeHandicapPartner(result.getPlayerHomePartner().getHandicap(result.getSeason()));
              }
              if (result.getPlayerAwayHandicapPartner()  == null && result.getPlayerAwayPartner() != null) {
                  result.setPlayerAwayHandicapPartner(result.getPlayerAwayPartner().getHandicap(result.getSeason()));
              }
              leagueService.save(result);
          }
      }

      public void scrambleGameType() {
        Map<LocalDate,List<TeamMatch>> matches = leagueService.findAll(TeamMatch.class).parallelStream()
                .filter(tm -> tm.getSeason().isScramble())
                .filter(tm -> !tm.getSeason().isActive())
                .collect(Collectors.groupingBy(
                        tm -> tm.getMatchDate().toLocalDate()
                ));
        Division division = Division.MIXED_EIGHT;
        for (LocalDate localDate : matches.keySet().stream().sorted(new Comparator<LocalDate>() {
            @Override
            public int compare(LocalDate o1, LocalDate o2) {
                return o1.compareTo(o2);
            }
        }).collect(Collectors.toList())) {

            for (TeamMatch teamMatch : matches.get(localDate)) {
                teamMatch.setDivision(division);
                logger.info("Setting game type to " + division);
                leagueService.save(teamMatch);
            }
            division = division == Division.MIXED_EIGHT ? Division.MIXED_NINE : Division.MIXED_EIGHT;
        }
    }

}
