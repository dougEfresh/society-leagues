package com.society.leagues;

import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class SchemaData {

    public static boolean generated = false;
    private static Logger logger = LoggerFactory.getLogger(SchemaData.class);
    @Value("${generate}")
    boolean generate = false;

    @Autowired
    SeasonDao seasonApi;
    @Autowired
    DivisionDao divisionApi;
    @Autowired
    TeamDao teamApi;
    @Autowired
    PlayerDao playerApi;
    @Autowired
    UserDao userApi;
    @Autowired
    TeamMatchDao matchApi;
    @Autowired
    PlayerResultDao playerResultApi;
    @Autowired
    TeamResultDao teamResultApi;
    @Autowired
    ChallengeDao challengeApi;
    @Autowired
    SlotDao slotApi;

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

        for (int i = 0; i <= USERS.length; i++) {
            Team team = new Team(String.format("team%2d", i));
            teamApi.create(team);
        }

        createUsers();
        createChallengePlayers();
        createChallengeRequests();
        createChallengeTeamMatches();
        createMatchResults();
    }

    private void createUsers() {
        for (int i = 0; i < USERS.length; i++) {
            User user = new User();
            user.setFirstName(USERS[i].split(",")[0]);
            user.setLastName(USERS[i].split(",")[1]);
            user.setEmail(i + "@example.com");
            user.setPassword("login" + i);
            user.setLogin("login" + i);
            user.addRole(Role.PLAYER);
            userApi.create(user);
        }
    }

    private void createChallengePlayers() {
        for (int i = 0; i < USERS.length; i++) {
		int num = i;
            for (Division division : divisionApi.get().stream().filter(d -> d.isChallenge()).collect(Collectors.toList())) {
                Player player = new Player();
                String teamName = "login" + num;
                Team team = teamApi.get(teamName);
                if (team == null)
                    player.setTeam(teamApi.create(new Team(teamName)));
                else
                    player.setTeam(team);

                player.setDivision(division);
                player.setSeason(seasonApi.get(division.getType().name()));
                player.setStart(new Date());
                player.setHandicap(getRandomHandicap(i, division.getType()));
                player.setUser(userApi.get("login" + num));
                if (player.getUser() == null) {
                    throw new RuntimeException("Error find login for login" + i + " player: " + player);
                }
                playerApi.create(player);
            }
        }
        logger.info("Created " + playerApi.get().size() + " challenge players");
    }

    private void createChallengeRequests(Player player) {
        for(int i=0; i<5; i++) {
            List<Player> potentials = challengeApi.getPotentials(player.getUserId()).stream().filter(p -> p.getDivision().equals(player.getDivision())).collect(Collectors.toList());
            int slot = (int) Math.round(Math.random() * potentials.size()) - 1;
            Player opponent = potentials.get(slot == -1 ? 0 : slot);
            Challenge challenge = new Challenge();

            List<Slot> slots = slotApi.get(LocalDateTime.now().plusDays((int) Math.round(Math.random()*20)));
            slot = (int) Math.round(Math.random() * slots.size()) - 1;
            challenge.setSlot(slots.get(slot == -1 ? 0 : slot));
            challenge.setStatus(Status.PENDING);
            challenge.setOpponent(opponent);
            challenge.setChallenger(player);
            challengeApi.requestChallenge(challenge);
        }
          for(int i=0; i<10; i++) {
            List<Player> potentials = challengeApi.getPotentials(player.getUserId()).stream().filter(p -> p.getDivision().equals(player.getDivision())).collect(Collectors.toList());
            int slot = (int) Math.round(Math.random() * potentials.size()) - 1;
            Player opponent = potentials.get(slot == -1 ? 0 : slot);
            Challenge challenge = new Challenge();

            List<Slot> slots = slotApi.get(LocalDateTime.now().minusWeeks((int) Math.round(Math.random() * 20)));
            slot = (int) Math.round(Math.random() * slots.size()) - 1;
            challenge.setSlot(slots.get(slot == -1 ? 0 : slot));
            challenge.setStatus(Status.ACCEPTED);
            challenge.setOpponent(opponent);
            challenge.setChallenger(player);
            challengeApi.requestChallenge(challenge);
        }
    }

    private void createChallengeRequests() {
        List<Player> players = playerApi.get().stream().
                filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());
        for (Player player : players) {
            createChallengeRequests(player);
            acceptChallengeRequest(player);
            setNeedNotifyChallengeRequest(player);
            setCancelChallengeRequest(player);
        }
        for (int i = 0; i < Status.values().length; i++) {
            Status status = Status.values()[i];
            logger.info("Challenge Status:  " +
                            status + " " +
                            challengeApi.get().stream().filter(c -> c.getStatus() == status).count()
            );
        }
    }

    private void acceptChallengeRequest(Player player) {
        Challenge challenge = challengeApi.get().stream().
                filter(c -> c.getChallenger().equals(player)).
                filter(c -> c.getStatus() == Status.PENDING).
                findFirst().orElse(null);
        challengeApi.acceptChallenge(challenge);
    }

    private void setNeedNotifyChallengeRequest(Player player) {
        Challenge challenge = challengeApi.get().stream().
                filter(c -> c.getChallenger().equals(player)).
                filter(c -> c.getStatus() == Status.PENDING).
                filter( c-> c.getSlot().getLocalDateTime().isAfter(LocalDateTime.now())
                ).
                findFirst().orElse(null);
        if (challenge == null) {
            throw new RuntimeException("Couldn't find accepted challenge for " + player.getId());
        }
        challenge.setStatus(Status.NEEDS_NOTIFY);
        challengeApi.modifyChallenge(challenge);
    }

    private void setCancelChallengeRequest(Player player) {
        Challenge challenge = challengeApi.get().stream().
                filter(c -> c.getChallenger().equals(player)).
                filter(c -> c.getStatus() == Status.PENDING).
                filter( c-> c.getSlot().getLocalDateTime().isAfter(LocalDateTime.now())
                ).
                findFirst().orElse(null);
        if (challenge == null) {
            throw new RuntimeException("Couldn't find accepted challenge for " + player.getId());
        }
        challenge.setStatus(Status.CANCELLED);
        challengeApi.modifyChallenge(challenge);
    }
    private void createChallengeTeamMatches() {
        for(DivisionType divisionType : DivisionType.values()) {
            if (!DivisionType.isChallange(divisionType)) {
                continue;
            }
            List<Challenge> challenges = challengeApi.get().stream().
                    filter(c -> c.getChallenger().getDivision().getType() == divisionType).
                    filter(c -> c.getSlot().getLocalDateTime().isBefore(LocalDateTime.now().plusDays(1))).
                    filter(c -> c.getStatus() == Status.ACCEPTED).
                    collect(Collectors.toList());

            logger.info("Create match results for " + challenges.size() + " challenges");
            for (Challenge challenge : challenges) {
                Player challenger = challenge.getChallenger();
                TeamMatch teamMatch = new TeamMatch();
                teamMatch.setHome(challenger.getTeam());
                teamMatch.setDivision(challenger.getDivision());
                teamMatch.setSeason(challenger.getSeason());
                teamMatch.setMatchDate(challenge.getSlot().getLocalDateTime());
                teamMatch.setAway(challenge.getOpponent().getTeam());
                matchApi.create(teamMatch);
            }

            logger.info("Create a total of " + matchApi.get().size() + " matches");
        }
    }

    private void createMatchResults() {
        List<TeamMatch> teamMatches = matchApi.get().stream().collect(Collectors.toList());
        logger.info("Creating match results " +  teamMatches.size());
        Collection<Player> players = playerApi.get();

        for (TeamMatch teamMatch : teamMatches) {
            TeamResult result = new TeamResult();
            result.setTeamMatch(teamMatch);

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

            PlayerResult savedResult = playerResultApi.create(playerResult);
            if (savedResult == null) {
                throw new RuntimeException("Could not create player result for " + playerResult);
            }
        }

        logger.info("Created  " + playerResultApi.get().size() + " match results");
    }

    private Handicap getRandomHandicap(int i, DivisionType divisionType) {
        if (i <= Math.floor(USERS.length * .25)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.DPLUS;
            else
                return Handicap.FIVE;
        }

        if (i <= Math.floor(USERS.length * .45)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.D;
            else
                return Handicap.FOUR;
        }

        if (i <= Math.floor(USERS.length * .60)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.C;
            else
                return Handicap.THREE;
        }

        if (i <= Math.floor(USERS.length * .70)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.B;
            else
                return Handicap.SIX;
        }

        if (i <= Math.floor(USERS.length * .80)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.BPLUS;
            else
                return Handicap.TWO;
        }

        if (i <= Math.floor(USERS.length * .9)) {
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

    static String[] USERS = new String[]{
            "Doug,Rhee",
            "Rob,P",
            "Jared,P",
            "Doug,Chimento",

            "Akiko,Sugiyama",
            "Alex,Kittler",
            "Andrew,Footer",
            "Anna,Kaplan",
            "Bob,Hemnami",
            "Chris,Spencer",
            "Chumrean,Sacharitakul",
            "Dan,Faraguna",
            "David,Allen",
            "Dev,Chatterjee",

            "Giovanni,Mata",
            "Izac,Horne",
            "James,Taylor",

            "Jeff,Tischler",
            "Jin,Gong",
            "John,MacArthur",
            "Jonathan,Smith",
            "Mark,McDade",
            "Max,Watanabe",
            "Michael,Harrington",
            "Mimi,Ji",
            "Nick,Meyer",
            "Olga,Nikolaeva",
            "Oliver,Stalley",
            "Rob,Mislivets",
            "Serafina,Shishkova",
            "Soham,Patel",
            "Thomas,Wan",
            "Vinay,Pai",
            "Vinny,Ferri",
            "Wanlop,Chan",
            "Yorgos,Hatziefhimiou",
            "Zain,Siddiqi",
            "Thomas,Wan",
            "Eric,Adelman"


	    /*,
            "Samms,Hasburn",

            "Larry,Busacca",
            "Edward,Lum",
            "Michael,Secondo",
            "Thomas,Wan",
            "Eric,Adelman",
            "Naoto,Hariu",
            "Peter,Khan",
            "Tejune,Kang",
            "Ross,Robbins",
            "Todd,Wilson",
            "Oscar,Ortiz",
            "Henry,Balingcongan",
            "Keith,Diaz",
            "Paul,Lyons",
            "Quang,Nguyen",
            "Sanjay,Sachdeva",
            "Thomas,Scott",
            "Ramilo,Tanglao",
            "Ben,Castaneros",
            "Ron,Gabia",


            "Sherwin,Robinson",
            "Paul,Johnson",
            "Cassie,Corbin",
            "Ambi,Estevez",

            "Matthew,Harricharan",
            "Miguel,Laboy",
            "Rene,Villalobos"
	    */
    };
}
