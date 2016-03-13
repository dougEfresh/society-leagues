package com.society.leagues.model;

import com.society.leagues.CopyUtil;
import com.society.leagues.client.api.domain.Stat;

import java.util.List;

public class UserStat extends Stat {

    public static List<UserStat> userStats(List<Stat> teams) {
        return CopyUtil.copy(teams, UserStat.class);
    }

}
