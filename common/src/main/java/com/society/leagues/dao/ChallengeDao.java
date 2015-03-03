package com.society.leagues.dao;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChallengeDao extends Dao<Challenge> implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired PlayerDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired TeamMatchDao teamMatchDao;
    @Autowired UserDao userDao;

    @Value("${email-override:}") String emailOverride;
    
    @Override
    public List<User> getPotentials(Integer id) {
        try {
            User challenger = userDao.get(id);

            List<Player> potentials = new ArrayList<>();
            List<Player> challengePlayers = playerDao.getByUser(challenger).stream().filter(
                    p -> p.getDivision().isChallenge()).collect(Collectors.toList()
            );
            List<Player> players = playerDao.get();
            for (Player challengePlayer : challengePlayers) {
                potentials.addAll(findChallengeUsers(challengePlayer.getDivision(),challengePlayer.getHandicap(),players));
            }
            potentials = potentials.stream().filter(p -> !Objects.equals(p.getUserId(), id)).collect(Collectors.toList());
            Map<Integer,User> opponents = new HashMap<>();

            for (Player potential : potentials) {
                if (!opponents.containsKey(potential.getUserId())) {
                    opponents.put(potential.getUserId(), new User(potential.getUserId()));
                }
                opponents.get(potential.getUserId()).addPlayer(potential);
            }
            return  Arrays.asList(opponents.values().toArray(new User[]{}));
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return Collections.emptyList();
    }
    
    @Override
    public Challenge requestChallenge(final Challenge challenge) {
        TeamMatch teamMatch = teamMatchDao.create(challenge.getTeamMatch());
        if (teamMatch == null)
            return null;

        challenge.setTeamMatch(teamMatch);
        PreparedStatementCreator ps = con ->
        {
            PreparedStatement st  = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1,challenge.getTeamMatch().getId());
            st.setDate(2, new java.sql.Date(challenge.getChallengeDate().getTime()));
            st.setString(3, Status.PENDING.name());
            return st;
        } ;
        return create(challenge,ps);
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        try {
            jdbcTemplate.update("UPDATE challenge SET status  = ? WHERE challenge_id = ?", 
                    Status.ACCEPTED.name(), challenge.getId());
            challenge.setStatus(Status.ACCEPTED);
        } catch (Throwable t ){ 
            logger.error(t.getLocalizedMessage(),t);
            return null;
        }
        return challenge;
    }
    
    @Override
    public List<Challenge> listChallenges(Integer userId) {
        User u  = userDao.get(userId);
        if (u == null)
            return Collections.emptyList();

        Set<Player> players = u.getPlayers();

        if (players == null || players.isEmpty())
            return Collections.emptyList();

        players = players.stream().filter(p -> p.getDivision().isChallenge()).collect(Collectors.toSet());
        List<Challenge> challenges = new ArrayList<>();

        for (final Player p: players) {
            challenges.addAll(get().stream().filter(c ->
                            c.getTeamMatch().getHome().equals(p.getTeam()) ||
                                    c.getTeamMatch().getAway().equals(p.getTeam())
            ).collect(Collectors.toList()));

        }
        return challenges;
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        challenge.setStatus(Status.CANCELLED);
        return modifyChallenge(challenge) != null;
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return modify(challenge,"update challenge set " +
                        "team_match_id=?," +
                        "challenge_date=?," +
                        "status=? " +
                        "where challenge_id = ?",
                challenge.getTeamMatch().getId(),
                challenge.getChallengeDate(),
                challenge.getStatus().name(),
                challenge.getId());
    }

    @Override
    public List<Slot> slots(Date date) {
        return null;
    }

    private List<Player> findChallengeUsers(Division division,Handicap handicap, List<Player> players) {
        return players.stream().
                filter(p -> p.getDivision().getId().equals(division.getId()) &&
                        p.getHandicap().ordinal() >= handicap.ordinal()-3 &&
                        p.getHandicap().ordinal() <= handicap.ordinal()+3).
                collect(Collectors.toList());
    }

    @Override
    public List<Challenge> getByPlayer(Player p) {
        if (p == null) {
            return Collections.emptyList();
        }
        Team t = p.getTeam();

        return get().stream().
                filter(c -> c.getTeamMatch().getHome().equals(t) ||
                        c.getTeamMatch().getAway().equals(t)
                ).
                filter(c -> c.getTeamMatch().getDivision().equals(p.getDivision())).
                collect(Collectors.toList());
    }

    @Override
    public RowMapper<Challenge> getRowMapper() {
        return mapper;
    }

    final RowMapper<Challenge> mapper = (rs, rowNum) -> {
        Challenge challenge = new Challenge();
        challenge.setChallengeDate(rs.getDate("challenge_date"));
        challenge.setStatus(Status.valueOf(rs.getString("status")));
        challenge.setId(rs.getInt("challenge_id"));
        challenge.setTeamMatch(teamMatchDao.get(rs.getInt("team_match_id")));
        return challenge;
    };
    
    final static String CREATE = "INSERT INTO challenge(team_match_id,challenge_date,status) VALUES (?,?,?)";


    @Override
    public String getSql() {
        return "select * from challenge";
    }

}
