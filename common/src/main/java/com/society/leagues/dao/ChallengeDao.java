package com.society.leagues.dao;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
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
public class ChallengeDao extends ClientDao<Challenge> implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired Dao dao;
    @Autowired UserDao userDao;
    @Autowired PlayerDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;
    @Autowired MatchDao matchDao;

    @Value("${email-override:}") String emailOverride;
    
    @Override
    public List<User> getPotentials(Integer id) {
        try {
            User challenger = userDao.get(id);
            List<Player> potentials = new ArrayList<>();
            potentials.addAll(getHandicapPlayers(challenger, DivisionType.EIGHT_BALL_CHALLENGE));
            potentials.addAll(getHandicapPlayers(challenger, DivisionType.NINE_BALL_CHALLENGE));
            potentials = potentials.stream().filter(p -> !Objects.equals(p.getUser().getId(), id)).collect(Collectors.toList());
            Map<Integer,User> opponents = new HashMap<>();
            for (Player potential : potentials) {
                if (!opponents.containsKey(potential.getUser().getId())) {
                    opponents.put(potential.getUser().getId(),potential.getUser());
                }
                
                User u  = opponents.get(potential.getUser().getId());
                potential.setUser(null);
                u.addPlayer(potential);
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
        return dao.create(challenge,ps);
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
        String sql = "select c.* from challenge  c left join player cp on c.challenger_player_id = cp.player_id " +
                " left join player op on c.opponent_player_id = op.player_id " +
                "where cp.user_id = ? or op.user_id=?";
        List<Map<String,Object>> c = dao.get(sql,userId,userId);
        ArrayList<Challenge> challenges = new ArrayList<>();
        for (Map<String, Object> rs : c) {
            Integer id = (Integer) rs.get("challenge_id");
            Challenge challenge = new Challenge();
            challenge.setId(id);
            challenge.setOpponent(playerDao.get((Integer) rs.get("opponent_player_id")));
            challenge.setChallenger(playerDao.get((Integer) rs.get("challenge_player_id")));
            challenge.setStatus(Status.valueOf(rs.get("status").toString()));
            if (rs.get("team_match_id") != null) {
                challenge.setMatch(matchDao.get((Integer) rs.get("team_match_id")));
            }
            Slot slot = new Slot();
            slot.setId((Integer) rs.get("slot"));
            slot.setDate((Date) rs.get("challenge_date"));
            challenge.setSlot(slot);
            challenges.add(challenge);
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
        return dao.modify(challenge,"update challenge set challenger_player_id=?," +
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
        return null;
    }

    private List<Player> getHandicapPlayers(User user, DivisionType division) {
        if (user.getPlayers() == null || user.getPlayers().isEmpty())
            return Collections.emptyList();
        
        for (Player player : user.getPlayers()) {
            if (player.getDivision().getType() != division)
                continue;
            return playerDao.findHandicapRange(player.getDivision(),
                    player.getHandicap().ordinal()-3,
                    player.getHandicap().ordinal()+3);
        }
        return Collections.emptyList();
    }

    @Override
    public RowMapper<Challenge> getRowMapper() {
        return mapper;
    }

    @Override
    public List<Challenge> get() {
        return null;
    }

    final static RowMapper<Challenge> mapper = (rs, rowNum) -> {
        Challenge challenge = new Challenge();
        
        return null;
    };
    
    final static String CREATE = "INSERT INTO challenge(challenger_player_id," +
            "opponent_player_id,slot,challenge_date,status) VALUES (?,?,?,?,?)";
    

    
}
