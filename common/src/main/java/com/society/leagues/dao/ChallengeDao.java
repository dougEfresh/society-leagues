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
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class ChallengeDao extends Dao<Challenge> implements ChallengeApi {
    static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired PlayerDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired UserDao userDao;

    @Value("${email-override:}") String emailOverride;
    
    @Override
    public List<Player> getPotentials(Integer id) {
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
            //Exclude the user requesting potentials
            potentials = potentials.stream().filter(p -> !Objects.equals(p.getUserId(), id)).collect(Collectors.toList());
            return potentials;
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
            st.setInt(1, challenge.getChallenger().getId());
            st.setInt(2, challenge.getOpponent().getId());
            st.setTimestamp(3,Timestamp.valueOf(challenge.getChallengeDate()));
            st.setString(4, Status.PENDING.name());
            return st;
        } ;
        return create(challenge,ps);
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        return modify(challenge,"UPDATE challenge SET status  = ? WHERE challenge_id = ?", Status.ACCEPTED.name(), challenge.getId());
    }
    
    @Override
    public List<Challenge> listChallenges(Integer userId) {
        User u  = userDao.get(userId);
        if (u == null)
            return Collections.emptyList();

        return get().stream().filter(c-> c.getOpponent().getUser().equals(u) || c.getChallenger().getUser().equals(u)).collect(Collectors.toList());
    }

    public Challenge cancelChallenge(Challenge challenge) {
        return modify(challenge,"UPDATE challenge SET status  = ? WHERE challenge_id = ?", Status.CANCELLED.name(), challenge.getId());
    }

    public List<Challenge> getChallenges(User u, Status status) {
        List<Player> players = playerDao.getByUser(u);
        List<Challenge> challenges = new ArrayList<>();
        for (Player player : players) {
            challenges.addAll(getByPlayer(player).stream().filter(c -> c.getStatus() == status).collect(Collectors.toList()));
        }
        return  challenges;
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return modify(challenge, "update challenge set " +
                        "player_challenger_id=?," +
                        "player_opponent_id=?," +
                        "challenge_date=?," +
                        "status=? " +
                        "where challenge_id = ?",
                challenge.getChallenger().getId(),
                challenge.getOpponent().getId(),
                challenge.getChallengeDate(),
                challenge.getStatus().name(),
                challenge.getId());
    }

    public List<LocalDateTime> slots(LocalDateTime date) {
        return Slot.getDefault(date);
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
        return get().stream().
                filter(c -> c.getChallenger().equals(p) ||
                                c.getOpponent().equals(p)
                ).collect(Collectors.toList());
    }

    @Override
    public RowMapper<Challenge> getRowMapper() {
        return mapper;
    }

    final RowMapper<Challenge> mapper = (rs, rowNum) -> {
        Challenge challenge = new Challenge();
        challenge.setChallengeDate(rs.getTimestamp("challenge_date").toLocalDateTime());
        challenge.setStatus(Status.valueOf(rs.getString("status")));
        challenge.setId(rs.getInt("challenge_id"));
        challenge.setChallenger(playerDao.get(rs.getInt("player_challenger_id")));
        challenge.setChallenger(playerDao.get(rs.getInt("player_opponent_id")));
        return challenge;
    };
    
    final static String CREATE = "INSERT INTO challenge(player_challenger_id,player_opponent_id,challenge_date,status) VALUES (?,?,?,?)";

    @Override
    public String getSql() {
        return "select * from challenge";
    }

}
