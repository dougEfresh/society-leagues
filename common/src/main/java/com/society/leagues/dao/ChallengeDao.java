package com.society.leagues.dao;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChallengeDao extends Dao<Challenge> implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired PlayerDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MatchDao matchDao;
    @Autowired UserDao userDao;
    @Autowired DivisionDao divisionDao;

    @Value("${email-override:}") String emailOverride;
    
    @Override
    public List<User> getPotentials(Integer id) {
        try {
            User challenger = userDao.get(id);
            List<Player> potentials = new ArrayList<>();
            List<Player> challengePlayers = challenger.getPlayers().stream().filter(
                    p -> p.getDivision().isChallenge()).collect(Collectors.toList()
            );
            for (Player challengePlayer : challengePlayers) {
                potentials.addAll(findChallengeUsers(challengePlayer.getDivision(),challengePlayer.getHandicap()));
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
        PreparedStatementCreator ps = con ->
        {
            PreparedStatement st  = con.prepareStatement(CREATE, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1,challenge.getChallenger().getId());
            st.setInt(2,challenge.getOpponent().getId());
            st.setInt(3,challenge.getSlot().getId());
            st.setDate(4, new java.sql.Date(challenge.getSlot().getDate().getTime()));
            st.setString(5,Status.PENDING.name());
            return st;
        } ;
        return create(challenge,ps);
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        try {
            jdbcTemplate.update("UPDATE challenge SET status  = ? WHERE challenge_id = ?", 
                    Status.ACTIVE.name(), challenge.getId());
            challenge.setStatus(Status.ACTIVE);
        } catch (Throwable t ){ 
            logger.error(t.getLocalizedMessage(),t);
            return null;
        }
        return challenge;
    }
    
    @Override
    public List<Challenge> listChallenges(Integer userId) {
        List<Challenge> challenges = get().stream().filter(
                c -> Objects.equals(c.getChallenger().getUserId(), userId) ||
                        Objects.equals(c.getOpponent().getUserId(), userId)).
                collect(Collectors.toList());
        return challenges;
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        challenge.setStatus(Status.CANCELLED);
        return modifyChallenge(challenge) != null;
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return modify(challenge,"update challenge set challenger_player_id=?," +
                        "opponent_player_id=?," +
                        "slot=?," +
                        "challenge_date=?," +
                        "status=? " +
                        "where challenge_id = ?",
                challenge.getChallenger().getId(),
                challenge.getOpponent().getId(),
                challenge.getSlot().getId(),
                challenge.getSlot().getDate(),
                challenge.getStatus().name(),
                challenge.getId());
    }

    @Override
    public List<Slot> slots(Date date) {
        return Slot.getDefault(date);
    }

    private List<Player> findChallengeUsers(Division division,Handicap handicap) {
        return playerDao.get().stream().
                filter(p -> p.getDivision().getId().equals(division.getId()) &&
                        p.getHandicap().ordinal() >= handicap.ordinal()-3 &&
                        p.getHandicap().ordinal() <= handicap.ordinal()+3).
                collect(Collectors.toList());
    }

    @Override
    public List<Challenge> getByPlayer(Integer id) {
        return get().stream().filter(c -> c.getOpponent().getId().equals(id) || c.getChallenger().getId().equals(id)).
                collect(Collectors.toList());
    }

    @Override
    public RowMapper<Challenge> getRowMapper() {
        return mapper;
    }

    final RowMapper<Challenge> mapper = (rs, rowNum) -> {
        Challenge challenge = new Challenge();
        Player challenger = playerDao.get(rs.getInt("challenger_player_id"));
        Player opponent = playerDao.get(rs.getInt("opponent_player_id"));
        Slot slot = new Slot();
        slot.setId(rs.getInt("slot"));
        slot.setDate(rs.getDate("challenge_date"));
        challenge.setSlot(slot);
        challenge.setStatus(Status.valueOf(rs.getString("status")));
        challenge.setChallenger(challenger);
        challenge.setOpponent(opponent);
        challenge.setId(rs.getInt("challenge_id"));
        challenge.setMatch(matchDao.get(rs.getInt("team_match_id")));
        return challenge;
    };
    
    final static String CREATE = "INSERT INTO challenge(challenger_player_id," +
            "opponent_player_id,slot,challenge_date,status) VALUES (?,?,?,?,?)";


    @Override
    public String getSql() {
        return "select * from challenge";
    }

}
