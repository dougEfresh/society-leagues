package com.society.leagues;

import com.society.leagues.client.api.domain.Role;
import com.society.leagues.client.api.domain.*;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.client.api.domain.division.DivisionType;
import com.society.leagues.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class SchemaData {

    public static boolean generated = false;
    private static Logger logger = LoggerFactory.getLogger(SchemaData.class);
    
    @Autowired SeasonDao seasonApi;
    @Autowired DivisionDao divisionApi;
    @Autowired TeamDao teamApi;
    @Autowired PlayerDao playerApi;
    @Autowired UserDao userApi;
    @Autowired MatchDao matchAdminApi;
    
    static int NUM_PLAYERS = 80;
    
    public void generateData() {
        if (generated)
            return;

        generated = true;
        
        logger.info("***** generating data *****");
        
        for (DivisionType divisionType : DivisionType.values()) {
            divisionApi.create(new Division(divisionType));
            seasonApi.create(new Season(divisionType.name(), new Date(), -1, Status.ACTIVE));
        }
        
        for (int i = 1 ; i <= NUM_PLAYERS/10; i++) {
            Team team = new Team(String.format("team%2d",i));
            teamApi.create(team);
        }

        for (int i = 1 ; i <= NUM_PLAYERS ; i++){
            User user = new User();
            user.setFirstName("player_"+ i);
            user.setLastName(i + "");
            user.setEmail(i + "");
            user.setPassword("password" + i);
            user.setLogin("login" + i);
            user.addRole(Role.PLAYER);
            userApi.create(user);
        }

        for (int i = 1 ; i <= NUM_PLAYERS ; i++) {
            for (Division division : divisionApi.get()) {
                Player player = new Player();
                player.setUserId(userApi.getWithNoPlayer("login"+i).getId());
                String teamName = "team " + ((i % 8) + 1);
                player.setTeam(teamApi.get(teamName));
                player.setDivision(division);
                player.setSeason(seasonApi.get(division.getType().name()));
                player.setStart(new Date());
                player.setHandicap(getRandomHandicap(i,division.getType()));
                playerApi.create(player);
            }
        }
        /*
        Match match = new Match();
        User u1 = userApi.get("login1");
        User u2 = userApi.get("login2");
        Player p  = u1.getPlayers().stream().filter(pl -> pl.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().get();
        match.setHome(p.getTeam());
        p = u2.getPlayers().stream().filter(tm -> tm.getDivision().getType() == DivisionType.EIGHT_BALL_CHALLENGE).findFirst().get();
        match.setAway(p.getTeam());
        match.setMatchDate(new Date());
        match.setSeason(p.getSeason());
        matchAdminApi.create(match);
        */
    }
    
    private Handicap getRandomHandicap(int i, DivisionType divisionType) {
        if ( i <= Math.floor(NUM_PLAYERS*.25)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.DPLUS;
            else 
                return Handicap.FIVE;
        }
        
        if (i <= Math.floor(NUM_PLAYERS*.45)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.D;
            else
                return Handicap.FOUR;
        }
        
        if (i <= Math.floor(NUM_PLAYERS*.60)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.C;
            else
                return Handicap.THREE;
        }
        
        if (i <= Math.floor(NUM_PLAYERS*.70)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.B;
            else
                return Handicap.SIX;
        }

        if (i <= Math.floor(NUM_PLAYERS*.80)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.BPLUS;
            else
                return Handicap.TWO;
        }

        if (i <= Math.floor(NUM_PLAYERS*.9)) {
            if (divisionType.name().startsWith("NINE"))
                return Handicap.A;
            else
                return Handicap.SEVEN;
        }
        
        if (divisionType.name().startsWith("NINE"))
            return Handicap.APLUS;
        else
            return Handicap.EIGHT;
        
    }
}
