package com.society.leagues.resource;


import com.society.leagues.Service.LeagueService;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.mongo.*;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/convert")
public class ConvertResource {
    private static Logger logger = Logger.getLogger(ConvertResource.class);

    @Autowired LeagueService leagueService;
    @Autowired UserRepository userRepository;
    @Autowired SeasonRepository seasonRepository;
    @Autowired TeamRepository teamRepository;
    @Autowired TeamMatchRepository teamMatchRepository;
    @Autowired PlayerResultRepository playerResultRepository;
    @Autowired JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "user", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Boolean convertUser() {

        logger.info("Convert users");
        userRepository.deleteAll();
        List<Map<String,Object>> results = jdbcTemplate.queryForList("select\n" +
                " player_id,player_login,first_name,last_name, player_login ,\n" +
                "  case when player_login like '%no_login%' then 'INACTIVE' else 'ACTIVE' end  status\n" +
                "from player");
        for (Map<String, Object> result : results) {
            User u = new User();
            u.setLegacyId((Integer) (result.get("player_id")));
            u.setLogin(result.get("player_login").toString());
            u.setEmail(result.get("player_login").toString());
            u.setFirstName(result.get("first_name").toString());
            u.setLastName(result.get("last_name").toString());
            u.setStatus(Status.valueOf(result.get("status").toString()));
            u.setPassword(new BCryptPasswordEncoder().encode("abc123"));
            User user = leagueService.findByLogin(u.getLogin());
            if (user == null) {
                leagueService.save(u);
            }
        }
        return true;
    }

    @RequestMapping(value = "season", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Boolean converSeason() {
        logger.info("Convert season");
        seasonRepository.deleteAll();
        List<Map<String,Object>> results = jdbcTemplate.queryForList(
                "select m.season_id,concat(season_year,',',sn_name,',',league_game) as name,\n" +
                " min(match_start_date) as start_date, max(match_start_date) as end_date\n" +
                "from \n" +
                " match_schedule m\n" +
                " join season s on s.season_id=m.season_id\n" +
                " join season_name sn on s.season_number=sn.sn_id\n" +
                " join league l on m.league_id=l.league_id\n" +
                        " where match_start_date > '0000-00-00 00:00:00' " +
                " group by m.season_id,m.league_id;" );
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
                s.setDivision(Division.NINE_BALL_MIXED_MONDAYS);
            }
            if (s.getName().contains("9-ball")) {
                s.setDivision(Division.NINE_BALL_TUESDAYS);
            }
            s = leagueService.save(s);
            System.out.println(s.toString());
        }
        return true;
    }

    @RequestMapping(value = "teammatch", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.ALL_VALUE)
    public Boolean converTeamMatch() {
        logger.info("Convert team match");
        teamMatchRepository.deleteAll();
        teamRepository.deleteAll();
        List<Map<String,Object>> teams = jdbcTemplate.queryForList("select team_id,name from team");
        List<Map<String,Object>> matches = jdbcTemplate.queryForList("select match_id,m.season_id,home_team_id,visit_team_id,match_start_date from match_schedule m where match_start_date > 2000-01-01");
        List<Team>  teamRepo = new ArrayList<>(100);
        teamRepo = teamRepository.findAll();
        List<TeamMatch> teamMatchList = leagueService.findAll(TeamMatch.class);

        for (Map<String, Object> match : matches) {
            TeamMatch tm = new TeamMatch();
            tm.setLegacyId((Integer) match.get("match_id"));
            TeamMatch existing = teamMatchList.stream().filter(t->tm.getLegacyId().equals(t.getLegacyId())).findFirst().orElse(null);
            if (existing != null) {
                continue;
            }
            Timestamp ts = (Timestamp) match.get("match_start_date");
            tm.setMatchDate(ts.toLocalDateTime());
            final Integer home_team_id = (Integer) (match.get("home_team_id"));
            Map<String,Object> homeTeam = teams.stream().filter(t->t.get("team_id").equals(home_team_id)).findFirst().orElse(null);
            if (homeTeam == null) {
                continue;
            }

            final Integer away_team_id = (Integer) (match.get("visit_team_id"));
            Map<String,Object> away_team = teams.stream().filter(t->t.get("team_id").equals(away_team_id)).findFirst().orElse(null);
            if (away_team == null) {
                continue;
            }
            Season s = seasonRepository.findByLegacyId((Integer) match.get("season_id"));
            if (s == null) {
                continue;
            }
            tm.setSeason(s);
            Team h = teamRepo.stream().filter(
                    t->t.getSeason().equals(s)).
                    filter(t -> t.getName().equals(homeTeam.get("name"))).
                    findFirst().orElse(null);
            if (h == null) {
                h = new Team();
                h.setLegacyId((Integer) homeTeam.get("team_id"));
                h.setName(homeTeam.get("name").toString());
                h.setSeason(s);
                logger.info("Adding team " + h);
                h = leagueService.save(h);
                teamRepo.add(h);
            }
            tm.setHome(h);

            Team a = teamRepo.stream().filter(t -> t.getSeason().equals(s)).filter(t -> t.getName().equals(away_team.get("name"))).findFirst().orElse(null);
            if (a == null) {
                a = new Team();
                a.setLegacyId((Integer) away_team.get("team_id"));
                a.setName(away_team.get("name").toString());
                a.setSeason(s);
                logger.info("Adding team " + a);
                a = leagueService.save(a);
                teamRepo.add(a);
            }
            tm.setAway(a);
            leagueService.save(tm);
            //System.out.println(tm.toString());
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
                " \n" +
                "group by m.match_id,m.home_team_id) home \n" +
                "join\n" +
                "   (select m.match_id,m.visit_team_id,\n" +
                "      sum(games_won) as racks  from\n" +
                "    match_schedule m join result_ind rt on m.match_id=rt.match_id  and m.visit_team_id = rt.team_id\n" +
                "   group by m.match_id,m.visit_team_id\n" +
                "   ) away\n" +
                "on home.match_id=away.match_id");
        List<TeamMatch> teamMatchList = leagueService.findAll(TeamMatch.class);
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
            leagueService.save(tm);
        }
        System.out.println("Matches " + results.size() + " unmatched " + unmatch);
        return Boolean.TRUE;
    }

    public void convertPlayerResults() {
        logger.info("Player Results");
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
        List<TeamMatch> teamMatchList = leagueService.findAll(TeamMatch.class);
        List<User> users = leagueService.findAll(User.class);
        List<PlayerResult> playerResults = leagueService.findAll(PlayerResult.class);
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

            TeamMatch tm = teamMatchList.stream().filter(t->t.getLegacyId().equals((Integer)result.get("match_id"))).findFirst().orElse(null);
            if (tm == null) {
                continue;
            }
            r.setTeamMatch(tm);
            r.setSeason(tm.getSeason());
            User u  = users.stream().filter(user->user.getLegacyId().equals(result.get("player_id"))).findFirst().orElse(null);
            if (u == null) {
                continue;
            }
            Team home = r.getTeamMatch().getHome();
            Team away = r.getTeamMatch().getAway();

            if (result.get("home_or_away").equals("home")) {
                r.setPlayerHome(u);
                r.setHomeRacks((Integer) result.get("games_won"));
                r.setPlayerHomeHandicap(Handicap.get(result.get("hcd_name") == null ? null : result.get("hcd_name").toString()));
                home.addMember(u);
            } else {
                r.setPlayerAway(u);
                r.setAwayRacks((Integer) result.get("games_won"));
                r.setPlayerAwayHandicap(Handicap.get(result.get("hcd_name") == null ? null: result.get("hcd_name").toString() ));
                away.addMember(u);
            }
            r.setMatchNumber((Integer) result.get("match_number"));
            r = leagueService.save(r);
            playerResults.add(r);
            Team orgHome = leagueService.findOne(home);
            Team orgAway= leagueService.findOne(away);
            if (!away.getMembers().equals(orgAway.getMembers())) {
                r.getTeamMatch().setAway(leagueService.save(away));
                logger.info("Updating pr.getPlayerHome().equals(u) ? pr.getPlayerHomeHandicap() : pr.getPlayerAwayHandicap(team " + r.getTeamMatch().getAway().getName());
            }

            if (!home.getMembers().equals(orgHome.getMembers())) {
                r.getTeamMatch().setHome(leagueService.save(home));
                logger.info("Updating team " + r.getTeamMatch().getHome().getName());
            }

        }
    }

    public void findUserSeasons() {
        List<PlayerResult> results = leagueService.findAll(PlayerResult.class).stream().filter(pr -> pr.getSeason().getSeasonStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<Season> seasons = leagueService.findAll(Season.class).stream().
                filter(s -> s.getSeasonStatus() == Status.ACTIVE).collect(Collectors.toList());
        List<HandicapSeason> handicapSeasons = leagueService.findAll(HandicapSeason.class);
//        for(User u : leagueService.findAll(User.class).stream().filter(user->user.getLastName().equals("Chimento")).collect(Collectors.toList())) {
        for(User u : leagueService.findAll(User.class)) {
            u.setHandicapSeasons(new HashSet<>());
            User noHandicaps = leagueService.save(u);
            for (Season s : seasons) {
                PlayerResult pr = results.stream().
                        filter(r -> r.getSeason().equals(s)).
                        filter(r->r.getPlayerAway() != null && r.getPlayerHome() != null).
                        filter(r->r.getPlayerHome().equals(noHandicaps) || r.getPlayerAway().equals(noHandicaps)).
                        filter(r->r.getTeamMatch() != null).
                        filter(r->r.getTeamMatch().getMatchDate() != null).
                        sorted(new Comparator<PlayerResult>() {
                            @Override
                            public int compare(PlayerResult playerResult, PlayerResult t1) {
                                return t1.getTeamMatch().getMatchDate().compareTo(playerResult.getTeamMatch().getMatchDate());
                            }
                        }).findFirst().orElse(null);

                if (pr == null) {
                    //logger.info("No player results for " + u);
                    continue;
                }
                Handicap handicap = pr.getPlayerHome().equals(noHandicaps) ? pr.getPlayerHomeHandicap() : pr.getPlayerAwayHandicap();
                //HandicapSeason hs = new HandicapSeason(),s);
                HandicapSeason hs = handicapSeasons.stream().filter(h->h.getSeason().equals(s)).filter(h->h.getHandicap() == handicap).
                        findFirst().orElse(null);
                if (hs == null) {
                    hs = leagueService.save(new HandicapSeason(handicap,s));
                    handicapSeasons.add(hs);
                }
                noHandicaps.addHandicap(hs);
                leagueService.save(noHandicaps);
            }
        }
    }

}
