package com.society.leagues;

import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchemaData {

    public static boolean generated = false;
    private static Logger logger = LoggerFactory.getLogger(SchemaData.class);
    @Value("${generate}")
    boolean generate = false;

    @Autowired SeasonDao seasonApi;
    @Autowired DivisionDao divisionApi;
    @Autowired TeamDao teamApi;
    @Autowired PlayerDao playerApi;
    @Autowired UserDao userApi;
    @Autowired TeamMatchDao matchApi;
    @Autowired PlayerResultDao playerResultApi;
    @Autowired TeamResultDao teamResultApi;
    @Autowired ChallengeDao challengeApi;

    static int NUM_PLAYERS = 80;

    public void generateData() {
        if (!generate)
            return;

        if (generated)
            return;

        generated = true;
        
        logger.info("***** generating data *****");
        
        for (DivisionType divisionType : DivisionType.values()) {
            divisionApi.create(new Division(divisionType));
            seasonApi.create(new Season(divisionType.name(), new Date(), -1, Status.ACTIVE));
        }
        
        for (int i = 1 ; i <= NUM_PLAYERS/10; i++) {
            Team team = new Team(String.format("team%2d",i));
            teamApi.create(team);
        }

        createUsers();
        createChallengePlayers();
        createChallengeMatches(DivisionType.EIGHT_BALL_CHALLENGE);
        createChallengeMatches(DivisionType.NINE_BALL_CHALLENGE);
        createMatchResults();
        createChallengeRequests();
    }

    private void createUsers() {
        for (int i = 1 ; i <= NUM_PLAYERS ; i++){
            User user = new User();
            user.setFirstName("player_"+ i);
            user.setLastName("lastname_" + i);
            user.setEmail(i + "@example.com");
            user.setPassword("login" + i);
            user.setLogin("login" + i);
            user.addRole(Role.PLAYER);
            userApi.create(user);
        }
    }

    private void createChallengePlayers() {
        for (int i = 1 ; i <= NUM_PLAYERS ; i++) {
            for (Division division : divisionApi.get().stream().filter(d -> d.isChallenge()).collect(Collectors.toList())) {
                Player player = new Player();
                String teamName ="login"+i;
                Team team = teamApi.get(teamName);
                if (team == null)
                    player.setTeam(teamApi.create(new Team(teamName)));
                else
                    player.setTeam(team);

                player.setDivision(division);
                player.setSeason(seasonApi.get(division.getType().name()));
                player.setStart(new Date());
                player.setHandicap(getRandomHandicap(i, division.getType()));
                player.setUser(userApi.getWithNoPlayer("login" + i));
                if (player.getUser() == null) {
                    throw new RuntimeException("Error find login for login" +i + " player: "+ player);
                }
                playerApi.create(player);
            }
        }
        logger.info("Created " + playerApi.get().size() + " challenge players");
    }

    private void createChallengeRequests() {
        List<Player> players = playerApi.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());
        for (Player player : players) {
            List<User> potentials = challengeApi.getPotentials(player.getUserId());

            int slot = (int) Math.round(Math.random() * potentials.size())-1;
            List<Player> opponents = potentials.get(slot == -1 ? 0 : slot).getPlayers().stream().collect(Collectors.toList());
            slot = (int) Math.round(Math.random() * opponents.size())-1;
            Player opponent = opponents.get(slot == -1 ? 0 : slot);
            Challenge challenge = new Challenge();
            TeamMatch teamMatch = new TeamMatch();
            teamMatch.setDivision(player.getDivision());
            teamMatch.setSeason(player.getSeason());
            teamMatch.setHome(player.getTeam());
            teamMatch.setAway(opponent.getTeam());
            challenge.setTeamMatch(teamMatch);
            challenge.setChallengeDate(Slot.getDefault(LocalDateTime.now().plusDays(14)).get(2).getDate());
            challenge.setStatus(Status.PENDING);
            challengeApi.requestChallenge(challenge);
        }
        List<Challenge> pending = challengeApi.get().stream().filter(c -> c.getStatus() == Status.PENDING).collect(Collectors.toList());

        logger.info("Created " + pending.size() + " challenges");

        int i = 0;
        for (Challenge c: pending) {
            i++;
            if (i % 2 == 0)
                challengeApi.acceptChallenge(c);
        }

         logger.info("Accepted " + challengeApi.get().stream().filter(c -> c.getStatus() == Status.ACCEPTED).count() + " challenges");
    }

    private void createMatchResults() {
        Date before = new DateTime().plusDays(1).toDate();
        logger.info("Creating match results before " + before);

        List<TeamMatch> teamMatches = matchApi.get().stream().filter(m -> m.getMatchDate().before(before)).collect(Collectors.toList());
        List<Player> players = playerApi.get();

        for (TeamMatch teamMatch : teamMatches) {
            TeamResult result = new TeamResult();
            result.setTeamMatchId(teamMatch.getId());

            long home = Math.round(Math.random() * 10);
            long away = Math.round(Math.random() * 10);
            home = home == 0 ? 1 : home;
            away = away == 0 ? 1 : away;

            if (home == away)
                home++;

            result.setHomeRacks((int) home);
            result.setAwayRacks((int) away);
            result = teamResultApi.create(result);
            PlayerResult playerResult = new PlayerResult();

            Player playerHome = players.stream().filter(p -> p.getDivision().equals(teamMatch.getDivision()) &&
                    (teamMatch.getHome().equals(p.getTeam()))).
                    findFirst().orElse(null);

            Player playerAway = players.stream().filter(p -> p.getDivision().equals(teamMatch.getDivision()) &&
                    teamMatch.getAway().equals(p.getTeam())).
                    findFirst().orElse(null);

            playerResult.setPlayerAway(playerAway);
            playerResult.setPlayerHome(playerHome);
            playerResult.setAwayRacks(result.getAwayRacks());
            playerResult.setHomeRacks(result.getHomeRacks());
            playerResult.setTeamMatch(teamMatch);

            PlayerResult savedResult  = playerResultApi.create(playerResult);

            if (savedResult == null) {
                throw new RuntimeException("Could not create player result for " + playerResult);
            }

        }

        logger.info("Created  " + teamResultApi.get().size() + " match results");
    }

    private void createChallengeMatches(DivisionType divisionType) {
        List<Player> challengers = playerApi.get().stream().filter(p -> p.getDivision().getType() == divisionType).collect(Collectors.toList());
        int size = challengers.size();
        logger.info("Have " + size + " player matches to generate");
        for(int i=0; i<challengers.size(); i++) {
            Player challenger = challengers.get(i);
            List<Player> potentials = challengers.stream().filter(p -> !p.getId().equals(challenger.getId())).collect(Collectors.toList());
            for (int j = 1; j <= 5; j++) {
                TeamMatch teamMatch = new TeamMatch();
                teamMatch.setHome(challenger.getTeam());
                teamMatch.setDivision(challenger.getDivision());
                teamMatch.setSeason(challenger.getSeason());
                teamMatch.setMatchDate(new DateTime().minusDays(j * 7).toDate());
                int slot = (int) Math.round(Math.random() * potentials.size())-1;
                Player opponent = potentials.get(slot == -1 ? 0 : slot);
                teamMatch.setAway(opponent.getTeam());
                matchApi.create(teamMatch);
            }
        }
        logger.info("Create a total of " + matchApi.get().size() + " matches");

   }
    
    private Handicap getRandomHandicap(int i, DivisionType divisionType) {
        if ( i <= Math.floor(NUM_PLAYERS*.25)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.DPLUS;
            else 
                return Handicap.FIVE;
        }
        
        if (i <= Math.floor(NUM_PLAYERS*.45)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.D;
            else
                return Handicap.FOUR;
        }
        
        if (i <= Math.floor(NUM_PLAYERS*.60)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.C;
            else
                return Handicap.THREE;
        }
        
        if (i <= Math.floor(NUM_PLAYERS*.70)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.B;
            else
                return Handicap.SIX;
        }

        if (i <= Math.floor(NUM_PLAYERS*.80)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.BPLUS;
            else
                return Handicap.TWO;
        }

        if (i <= Math.floor(NUM_PLAYERS*.9)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.A;
            else
                return Handicap.SEVEN;
        }
        
        if (divisionType.name().startsWith("NINE"))
            return Handicap.APLUS;
        else
            return Handicap.EIGHT;
        
    }
}
