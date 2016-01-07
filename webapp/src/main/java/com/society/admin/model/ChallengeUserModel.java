package com.society.admin.model;

import com.society.leagues.client.api.domain.Handicap;
import com.society.leagues.client.api.domain.Team;
import com.society.leagues.client.api.domain.User;
import org.springframework.util.ReflectionUtils;

import java.util.ArrayList;
import java.util.List;


public class ChallengeUserModel extends Team {

    public static List<ChallengeUserModel> fromUsers(List<Team> users) {
        List<ChallengeUserModel> cu = new ArrayList<>();
        for (Team user : users) {
            ChallengeUserModel cum = new ChallengeUserModel();
            ReflectionUtils.shallowCopyFieldState(user,cum);
            cu.add(cum);
        }

        return cu;
    }

    public String display(User user) {
        return String.format("%s - %s - %s",
                getChallengeUser().getName(),
                getChallengeUser().getHandicap(getSeason()).getDisplayName(),
                Handicap.race(user.getHandicap(getSeason()),getChallengeUser().getHandicap(getSeason()))
        );
    }
}
