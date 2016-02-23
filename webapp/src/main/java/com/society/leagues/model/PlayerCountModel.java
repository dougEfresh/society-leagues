package com.society.leagues.model;


import com.society.leagues.client.api.domain.TeamMembers;
import com.society.leagues.client.api.domain.User;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;

public class PlayerCountModel extends User {

    int count;

    public void add() {
        count++;
    }

    public static List<PlayerCountModel> create(TeamMembers members) {
        List<PlayerCountModel> playerCountModelList = new ArrayList<>(members.getMembers().size());
        for (User user : members.getMembers()) {
            PlayerCountModel pc = new PlayerCountModel();
            ReflectionUtils.shallowCopyFieldState(user,pc);
            playerCountModelList.add(pc);
        }
         playerCountModelList.sort(User.sort);
        return playerCountModelList;
    }
}
