package com.society.test;

import com.society.leagues.client.ApiFactory;
import com.society.leagues.client.TeamApi;
import com.society.leagues.client.api.UserApi;
import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.UserClientApi;
import com.society.leagues.client.api.admin.*;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.client.api.domain.division.LeagueType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;


public class SchemaData {
    public static boolean generated = false;

    private String[] CHALLENGE_USERS = new String[]{"Doug_C", "Jeff_T", "Rob_P", "Doug_R", "Jared_P", "Howie_L", "Ken_S", "Alex_K",
                "Roger_P", "Saj_S", "Sofia_C", "Zack_Z", "Earl_S", "Efren_R", "John_S"};
    String[] eighthandicap = new String[]{"3", "3", "3", "4", "4", "5", "5", "6", "6", "6", "7", "7", "7", "8", "8"};
    String[] ninehandicap = new String[]{"D", "D", "D", "D+", "D+", "C", "C", "C+", "C+", "C", "B", "B", "B+", "A", "A+"};


    public static List<User> challengeUsers = new ArrayList<>();
    SeasonAdminApi seasonApi;
    DivisionAdminApi divisionApi;
    TeamApi teamApi;
    PlayerAdminApi playerApi;
    UserApi userApi;

    public void generateData(String url , String token) {
        if (generated)
            return;
        
        boolean debug = false;
        seasonApi = ApiFactory.createApi(SeasonAdminApi.class,token,url,debug);
        divisionApi  = ApiFactory.createApi(DivisionAdminApi.class,token,url,debug);
        teamApi  = ApiFactory.createApi(TeamApi.class,token,url,debug);
        userApi  = ApiFactory.createApi(UserApi.class,token,url,debug);
        playerApi =  ApiFactory.createApi(PlayerAdminApi.class,token,url,debug);


        Division eightBallChallenge = new Division(DivisionType.EIGHT_BALL_CHALLENGE, LeagueType.INDIVIDUAL);
        eightBallChallenge = divisionApi.create(eightBallChallenge);
        if (eightBallChallenge == null)
            throw new RuntimeException("Could not create 8ball Challenge Division");
        
        Division nineBallChallenge = new Division(DivisionType.NINE_BALL_CHALLENGE, LeagueType.INDIVIDUAL);
        nineBallChallenge = divisionApi.create(nineBallChallenge);
        if (nineBallChallenge == null)
            throw new RuntimeException("Could not create 8ball Challenge Division");

        Season challengeSeason = new Season("Challenge League", new Date(), -1);
        challengeSeason.setSeasonStatus(Status.ACTIVE);
        challengeSeason = seasonApi.create(challengeSeason);
        
        if (challengeSeason == null)
            throw new RuntimeException("Could not Challenge Season");

        createPlayerForSeason(challengeSeason,eightBallChallenge);
        createPlayerForSeason(challengeSeason,nineBallChallenge);
        generated = true;
        
    }
    
    private void createPlayerForSeason(Season season, Division division) {
        for (int i = 0; i < CHALLENGE_USERS.length; i++) {
            String user = CHALLENGE_USERS[i];
            String[] name = user.split("_");
            User u = userApi.get(user);
            if (u == null) {
                u = userApi.create(new User(name[0], name[1], user, user, Role.PLAYER));
                challengeUsers.add(u);
            }
            Team team = new Team(user, division);
            Team t = teamApi.get(user);
            if (t == null)
                t = teamApi.create(team);
            
            team = t;

            Player player = new Player();
            switch (division.getType()) {
                case EIGHT_BALL_MIXED_MONDAYS:
                case EIGHT_BALL_CHALLENGE:
                case EIGHT_BALL_THURSDAYS:
                case EIGHT_BALL_WEDNESDAYS:
                    player.setHandicap(eighthandicap[i]);
                    break;
                default:
                    player.setHandicap(ninehandicap[i]);
                    break;
            }
            player.setUser(u);
            player.setTeam(team);
            player.setStatus(Status.ACTIVE);
            player.setSeason(season);
            player.setDivision(division);
            playerApi.create(player);
        }

    }
}
