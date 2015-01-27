package com.society.leagues.dao;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.admin.SchedulerAdminApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;
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
@SuppressWarnings("unused")
public class ChallengeDao extends ClientDao<Challenge> implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired Dao dao;
    @Autowired UserDao userDao;
    @Autowired PlayerDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;
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
        String sql = "select * from challenge where user_id = ?";
        List<Map<String,Object>> c = dao.get(sql,userId);
        ArrayList<Challenge> challenges = new ArrayList<>();
        for (Map<String, Object> rs : c) {
            Integer id = (Integer) rs.get("challenge_id");
            Challenge challenge = new Challenge();
            challenge.setId(id);
            challenge.setOpponent(playerDao.get((Integer) rs.get("opponent_player_id")));
            challenge.setChallenger(playerDao.get((Integer) rs.get("challenge_player_id")));
            challenge.setStatus(Status.);
            if (rs.get("team_match_id") != null) {
                challenge.setMatch();
            }
            playerDao.get();
        }
        List<Player> players = playerDao.current(userId);
        if (players == null || players.isEmpty())
            return Collections.emptyList();
        
        players = players.stream().filter(p -> p.getDivision().getLeague() == LeagueType.INDIVIDUAL).collect(Collectors.toList());

        
        for (Player p : players) {
            
        }
        
        return null;
            
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        return null;
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return null;
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
    
    final static String LIST = "select ";
    
}
