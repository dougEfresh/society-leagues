package com.society.leagues.resource;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.admin.SchedulerAdminApi;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.ClientDao;
import com.society.leagues.dao.Dao;
import com.society.leagues.dao.PlayerDao;
import com.society.leagues.dao.UserDao;
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
public class ChallengeDao implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired Dao dao;
    @Autowired UserDao userDao;
    @Autowired PlayerDao playerDao;
    @Autowired SchedulerAdminApi schedulerDao;
    @Autowired JdbcTemplate jdbcTemplate;
    @Value("${email-override:}") String emailOverride;
    
    @Override
    public List<User> getPotentials(Integer id) {
        try {
            User challenger = userDao.get(id);
            List<Player> potentials = new ArrayList<>();
            potentials.addAll(getHandicapPlayers(challenger, DivisionType.EIGHT_BALL_CHALLENGE));
            potentials.addAll(getHandicapPlayers(challenger, DivisionType.NINE_BALL_CHALLENGE));
            potentials = potentials.stream().filter(p -> p.getUser().getId() != id).collect(Collectors.toList());
            Map<Integer,User> opponents = new HashMap<>();
            for (Player potential : potentials) {
                if (!opponents.containsKey(potential.getUser().getId())) {
                    opponents.put(potential.getUser().getId(),potential.getUser());
                }
                User u  = opponents.get(potential.getUser().getId());
                u.addPlayer(potential);
            }

            return  Arrays.asList(opponents.values().toArray(new User[] {}));
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
            return st;
        } ;
        return dao.create(challenge,ps);
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        Match match = new Match();
        match.setHome(challenge.getChallenger().getTeam());
        match.setAway(challenge.getOpponent().getTeam());
        match.setMatchDate(challenge.getSlot().getDate());
        match.setSeason(challenge.getChallenger().getSeason());
        match = schedulerDao.create(match);
        if (match == null ) {
            logger.error("Could not create match");
            return null;
        }
        
        challenge.setMatch(match);
        try {
            jdbcTemplate.update("UPDATE challenge SET team_match_id =? WHERE challenge_id = ?", 
                    match.getId(), challenge.getId());
        } catch (Throwable t ){ 
            logger.error(t.getLocalizedMessage(),t);
            return null;
        }
        return challenge;
    }
    

    @Override
    public List<Challenge> listChallenges(Integer userId) {
        String sql = ClientDao.CLIENT_REQUEST + "  " +
                "join challenge o on p.player_id=o.opponent_id where p.status = '" + Status.ACTIVE + 
                "' and s.status = '" + Status.ACTIVE + "' ";
        
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

    final static RowMapper<Challenge> mapper = (rs, rowNum) -> {
        Challenge challenge = new Challenge();
        return null;
    };
    
    final static String CREATE = "INSERT INTO challenge(challenger_player_id," +
            "opponent_player_id,slot,challenge_date) VALUES (?,?,?,?)";
}
