package com.society.leagues.resource;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.dao.ChallengeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.util.List;

@Component
@SuppressWarnings("unused")
@RolesAllowed(value = {"ADMIN","PLAYER"})
public class ChallengeResource extends ApiResource implements ChallengeApi {
    
    @Autowired ChallengeDao dao;
    
    @Override
    public List<Player> getPotentials(Integer id) {
        return dao.getPotentials(id);
    }
}
