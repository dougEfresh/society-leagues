package com.society.leagues.resource;

import com.society.leagues.client.api.ChallengeApi;
import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.ChallengeDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;
import java.util.Date;
import java.util.List;

@Component
@SuppressWarnings("unused")
@RolesAllowed(value = {"ADMIN","PLAYER"})
public class ChallengeResource extends ApiResource implements ChallengeApi {
    
    @Autowired ChallengeDao dao;
    
    @Override
    public List<User> getPotentials(Integer id) {
        return dao.getPotentials(id);
    }

    @Override
    public Challenge requestChallenge(Challenge challenge) {
        return dao.requestChallenge(challenge);
    }

    @Override
    public Challenge acceptChallenge(Challenge challenge) {
        return dao.acceptChallenge(challenge);
    }

    @Override
    public List<Challenge> listChallenges(Integer userId) {
        return dao.listChallenges(userId);
    }

    @Override
    public Boolean cancelChallenge(Challenge challenge) {
        return dao.cancelChallenge(challenge);
    }

    @Override
    public Challenge modifyChallenge(Challenge challenge) {
        return dao.modifyChallenge(challenge);
    }

    @Override
    public List<Slot> slots(Date date) {
        return dao.slots(date);
    }
}
