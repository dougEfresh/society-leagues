package com.society.leagues.model;


import com.society.leagues.client.api.domain.TeamMembers;
import com.society.leagues.client.api.domain.User;
import org.springframework.util.ReflectionUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;

public class PlayerCountModel extends User {

    List<LocalDateTime> playing = new ArrayList<>();
    List<LocalDateTime> matchDates = new ArrayList<>();

    public void add(LocalDateTime dt) {
        playing.add(dt);
    }

    public void addMatchDate(LocalDateTime dt) {
        matchDates.add(dt);
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

    public int getCount() {
        return playing.size();
    }

    public boolean isPlaying(LocalDateTime dt) {
        return playing.stream().filter(dt::isEqual).count() > 0;
    }

    public List<LocalDateTime> getMatchDates() {
        return matchDates;
    }
}
