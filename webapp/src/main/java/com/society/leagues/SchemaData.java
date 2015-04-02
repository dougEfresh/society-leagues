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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
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
        createChallengeMatches(DivisionType.EIGHT_BALL_CHALLENGE);
        createChallengeMatches(DivisionType.NINE_BALL_CHALLENGE);
        createMatchResults();
        createChallengeRequests();
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

    private void createChallengeRequests() {
        List<Player> players = playerApi.get().stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toList());
        for (Player player : players) {
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
        List<Challenge> pending = challengeApi.get().stream().filter(c -> c.getStatus() == Status.PENDING).collect(Collectors.toList());

        logger.info("Created " + pending.size() + " challenges");

        int i = 0;
        for (Challenge c : pending) {
            i++;
            if (i % 2 == 0)
                challengeApi.acceptChallenge(c);
        }

        logger.info("Accepted " + challengeApi.get().stream().filter(c -> c.getStatus() == Status.ACCEPTED).count() + " challenges");
    }

    private void createMatchResults() {
        LocalDate before = LocalDate.now().plusDays(1);
        Date b = new Date(before.getYear(), before.getMonthValue() - 1, before.getDayOfMonth());
        logger.info("Creating match results before " + before);


        List<TeamMatch> teamMatches = matchApi.get().stream().filter(m -> m.getMatchDate().before(b)).collect(Collectors.toList());
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

    private void createChallengeMatches(DivisionType divisionType) {
        List<Player> challengers = playerApi.get().stream().filter(p -> p.getDivision().getType() == divisionType).collect(Collectors.toList());
        int size = challengers.size();
        logger.info("Have " + size + " player matches to generate");
        for (int i = 0; i < challengers.size(); i++) {
            Player challenger = challengers.get(i);
            List<Player> potentials = challengers.stream().filter(p -> !p.getId().equals(challenger.getId())).collect(Collectors.toList());
            for (int j = 1; j <= 5; j++) {
                TeamMatch teamMatch = new TeamMatch();
                teamMatch.setHome(challenger.getTeam());
                teamMatch.setDivision(challenger.getDivision());
                teamMatch.setSeason(challenger.getSeason());
                teamMatch.setMatchDate(new Date(LocalDateTime.now().minusDays(j * 7).toEpochSecond(ZoneOffset.UTC)));
                int slot = (int) Math.round(Math.random() * potentials.size()) - 1;
                Player opponent = potentials.get(slot == -1 ? 0 : slot);
                teamMatch.setAway(opponent.getTeam());
                matchApi.create(teamMatch);
            }
        }
        logger.info("Create a total of " + matchApi.get().size() + " matches");
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
            "Mimi,Ji",
            "Bob,Hemnami",
            "Mark,McDade",
            "Nick,Meyer",
            "Chris,Spencer",
            "David,Allen",
            "Wanlop,Chan",
            "Izac,Horne",
            "Richard,Louapre",
            "Chumrean,Sacharitakul",
            "Akiko,Sugiyama",
            "Thomas,Wan",
            "Alex,Kittler",
            "Giovanni,Mata",
            "Soham,Patel",
            "Max,Watanabe",
            "Anna,Kaplan",
            "Andrew,Footer",
            "Bob,Hemnami",
            "Mark,McDade",
            "Nick,Meyer",
            "Abe,Shaw",
            "Chris,Spencer",
            "Richard,Louapre",
            "Michael,Harrington",
            "Anna,Kaplan",
            "Alex,Kittler",
            "Giovanni,Mata",
            "Soham,Patel",
            "Max,Watanabe",
            "Dev,Chatterjee",
            "Alex,Gomez",
            "Trevor,Heal",
            "Howie,Reuben",
            "Doug,Rhee",
            "Oliver,Stalley",
            "Ed,Sumner",
            "Vinny,Ferri",
            "Brendan,Ince",
            "Rob,Mislivets"
	    /*,
            "Zain,Siddiqi",
            "Jonathan,Smith",
            "Samms,Hasburn",
            "Larry,Busacca",
            "Edward,Lum",
            "Michael,Secondo",
            "James,Taylor",
            "Jeff,Tischler"
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
            "Yorgos,Hatziefhimiou",
            "John,MacArthur",
            "Sherwin,Robinson",
            "Paul,Johnson",
            "Doug,Chimento",
            "Cassie,Corbin",
            "Jin,Gong",
            "Vinay,Pai",
            "Serafina,Shishkova",
            "Olga,Nikolaeva",
            "Ambi,Estevez",
            "Dan,Faraguna",
            "Matthew,Harricharan",
            "Miguel,Laboy",
            "Rene,Villalobos"
	    */
    };
}
