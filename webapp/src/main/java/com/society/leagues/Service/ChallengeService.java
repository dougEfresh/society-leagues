package com.society.leagues.Service;

import com.society.leagues.mongo.ChallengRepository;
import com.society.leagues.mongo.SlotRepository;
import com.society.leagues.mongo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChallengeService  {

    @Autowired ChallengRepository challengRepository;
    @Autowired SlotRepository slotRepository;
    @Autowired UserRepository userRepository;


}
