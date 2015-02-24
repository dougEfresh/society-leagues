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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.List;
import java.util.Random;
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
    @Autowired MatchDao matchApi;
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

        for (int i = 1 ; i <= NUM_PLAYERS ; i++){
            User user = new User();
            user.setFirstName("player_"+ i);
            user.setLastName("lastname_" + i);
            user.setEmail(i + "@example.com");
            user.setPassword("password" + i);
            user.setLogin("login" + i + "@example.com");
            user.addRole(Role.PLAYER);
            userApi.create(user);
        }

        for (int i = 1 ; i <= NUM_PLAYERS ; i++) {
            for (Division division : divisionApi.get().stream().filter(d -> !d.isChallenge()).collect(Collectors.toList())) {
                Player player = new Player();
                player.setUserId(userApi.getWithNoPlayer("login"+i+"@example.com").getId());
                String teamName = "team " + ((i % 8) + 1);
                player.setTeam(teamApi.get(teamName));
                player.setDivision(division);
                player.setSeason(seasonApi.get(division.getType().name()));
                player.setStart(new Date());
                player.setHandicap(getRandomHandicap(i,division.getType()));
                playerApi.create(player);
            }

            for (Division division : divisionApi.get().stream().filter(d -> d.isChallenge()).collect(Collectors.toList())) {
                Player player = new Player();
                player.setUserId(userApi.getWithNoPlayer("login"+i+"@example.com").getId());
                String teamName ="login"+i+"@example.com";
                Team team = teamApi.get(teamName);
                if (team == null)
                    player.setTeam(teamApi.create(new Team(teamName)));
                else
                    player.setTeam(team);

                player.setDivision(division);
                player.setSeason(seasonApi.get(division.getType().name()));
                player.setStart(new Date());
                player.setHandicap(getRandomHandicap(i,division.getType()));
                playerApi.create(player);
            }
        }
        logger.info("Created " + playerApi.get().size() + " players");

        createChallengeMatches(DivisionType.EIGHT_BALL_CHALLENGE);
        createChallengeMatches(DivisionType.NINE_BALL_CHALLENGE);
        Date before = new DateTime().minusDays(8).toDate();
        logger.info("Creating match results before " + before);

        List<Match> matches = matchApi.get().stream().filter(m -> m.getMatchDate().before(before)).collect(Collectors.toList());

        for (Match match : matches) {
            TeamResult result = new TeamResult();
            result.setMatch(match);

            long home = Math.round(Math.random() * 10);
            long away = Math.round(Math.random() * 10);
            if (home == away)
                home++;

            result.setHomeRacks((int) home);
            result.setAwayRacks((int) away);
            teamResultApi.create(result);
        }

        logger.info("Created  " + teamResultApi.get().size() + " match results");

        for (Player player : playerApi.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList())) {
            List<User> potentials = challengeApi.getPotentials(player.getUserId());
            int slot = (int) Math.round(Math.random() * potentials.size())-1;
            List<Player> opponents = potentials.get(slot == -1 ? 0 : slot).getPlayers().stream().collect(Collectors.toList());
            slot = (int) Math.round(Math.random() * opponents.size())-1;
            Player opponent = opponents.get(slot == -1 ? 0 : slot);
            Challenge challenge = new Challenge();
            challenge.setChallenger(player);
            challenge.setOpponent(opponent);
            challenge.setSlot(Slot.getDefault(new DateTime().plusDays(14).toDate()).get(2));
            challenge.setStatus(Status.PENDING);
            challenge = challengeApi.requestChallenge(challenge);
        }
        logger.info("Created " + challengeApi.get().stream().filter(c -> c.getStatus() == Status.PENDING).collect(Collectors.toList()).size() + " challenges");
    }

   private void createChallengeMatches(DivisionType divisionType) {
       List<Player> challengers = playerApi.get().stream().filter(p -> p.getDivision().getType() == divisionType).collect(Collectors.toList());
        int size = challengers.size();
        logger.info("Have " + size + " player matches to generate");
        for(int i=0; i<challengers.size(); i++) {
            Player challenger = challengers.get(i);
            List<Player> potentials = challengers.stream().filter(p -> !p.getId().equals(challenger.getId())).collect(Collectors.toList());
            for (int j = 1; j <= 5; j++) {
                Match match = new Match();
                match.setHome(challenger.getTeam());
                match.setDivision(challenger.getDivision());
                match.setSeason(challenger.getSeason());
                match.setMatchDate(new DateTime().minusDays(j * 7).toDate());
                int slot = (int) Math.round(Math.random() * potentials.size())-1;
                Player opponent = potentials.get(slot == -1 ? 0 : slot);
                match.setAway(opponent.getTeam());
                matchApi.create(match);
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
