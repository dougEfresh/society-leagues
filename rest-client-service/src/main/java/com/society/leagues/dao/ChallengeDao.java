package com.society.leagues.dao;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@SuppressWarnings("unused")
public class ChallengeDao implements ChallengeApi {
    private static Logger logger = LoggerFactory.getLogger(ChallengeDao.class);
    @Autowired Dao dao;
    @Autowired UserDao userDao;
    @Autowired PlayerDao playerDao;
    @Autowired JdbcTemplate jdbcTemplate;
    
    @Override
    public List<Player> getPotentials(Integer id) {
        try {
            User challenger = userDao.get(id);
            List<Player> potentials = new ArrayList<>();
            potentials.addAll(getHandicapPlayers(challenger, DivisionType.EIGHT_BALL_CHALLENGE));
            potentials.addAll(getHandicapPlayers(challenger, DivisionType.NINE_BALL_CHALLENGE));
            return potentials.stream().filter(p -> p.getUser().getId() != id).collect(Collectors.toList());
        } catch (Throwable t) {
            logger.error(t.getLocalizedMessage(),t);
        }
        return Collections.emptyList();
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
}
