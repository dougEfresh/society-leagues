package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.client.views.PlayerResultView;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class PlayerResult  extends LeagueObject {

    @NotNull @DBRef TeamMatch teamMatch;
    @NotNull @DBRef User playerHome;
    @DBRef User playerAway;
    @NotNull Integer homeRacks = 0;
    @NotNull Integer awayRacks = 0;
    @NotNull Integer matchNumber = 0;
    @NotNull Handicap playerHomeHandicap = Handicap.UNKNOWN;
    @NotNull Handicap playerAwayHandicap = Handicap.UNKNOWN;
    User playerHomePartner;
    User playerAwayPartner;
    Handicap playerHomeHandicapPartner;
    Handicap playerAwayHandicapPartner;
    MatchPoints matchPoints;
    Team referenceTeam = null;
    User referenceUser = null;
    Boolean scotch = false;

    public PlayerResult() {
    }

    public PlayerResult(User home, User away, TeamMatch teamMatch) {
        this.playerHome = home ;
        this.playerAway = away;
        this.playerHomeHandicap = home.getHandicap(teamMatch.getSeason());
        this.playerAwayHandicap = away.getHandicap(teamMatch.getSeason());
        this.teamMatch = teamMatch;
    }

    public PlayerResult(TeamMatch teamMatch, User playerHome, User playerAway, Integer homeRacks, Integer awayRacks,
                        Integer matchNumber, Handicap playerHomeHandicap, Handicap playerAwayHandicap) {
        this.teamMatch = teamMatch;
        this.playerHome = playerHome;
        this.playerAway = playerAway;
        this.homeRacks = homeRacks;
        this.awayRacks = awayRacks;
        this.matchNumber = matchNumber;
        this.playerHomeHandicap = playerHomeHandicap;
        this.playerAwayHandicap = playerAwayHandicap;
    }

    public PlayerResult(String id) {
        this.id = id;
    }

    public Boolean isScotch() {
        if (playerAwayPartner != null || playerHomePartner != null)
            return true;

        return scotch;
    }

    public void setScotch(Boolean scotch) {
        this.scotch = scotch;
    }

    public TeamMatch getTeamMatch() {
        return teamMatch;
    }

    public void setTeamMatch(TeamMatch teamMatch) {
        this.teamMatch = teamMatch;
    }

    public User getPlayerHome() {
        return playerHome;
    }

    public void setPlayerHome(User playerHome) {
        this.playerHome = playerHome;
    }

    public User getPlayerAway() {
        return playerAway;
    }

    public void setPlayerAway(User playerAway) {
        this.playerAway = playerAway;
    }

    public Integer getHomeRacks() {
        return homeRacks == null ? 0 :homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    public Integer getAwayRacks() {
        return awayRacks == null ? 0 : awayRacks;
    }

    public void setAwayRacks(Integer awayRacks) {
        this.awayRacks = awayRacks;
    }

    public Integer getMatchNumber() {
        return matchNumber;
    }

    public void setMatchNumber(Integer matchNumber) {
        this.matchNumber = matchNumber;
    }

    @JsonView(PlayerResultView.class)
    public Season getSeason() {
        return teamMatch.getSeason();
    }

    public Handicap getPlayerHomeHandicap() {
        return playerHomeHandicap;
    }

    public void setPlayerHomeHandicap(Handicap playerHomeHandicap) {
        this.playerHomeHandicap = playerHomeHandicap;
    }

    public Handicap getPlayerAwayHandicap() {
        return playerAwayHandicap;
    }

    public void setPlayerAwayHandicap(Handicap playerAwayHandicap) {
        this.playerAwayHandicap = playerAwayHandicap;
    }

    public User getWinner() {
        if (homeRacks  == null || awayRacks == null)
            return playerHome;

        return homeRacks > awayRacks ? playerHome : playerAway;
    }

    public Integer getWinnerRacks() {
        if (homeRacks  == null ||  awayRacks == null)
            return 0;

        return homeRacks > awayRacks ? homeRacks : awayRacks;
    }

    public Handicap  getWinnerHandicap() {
        if (getWinner() == null)
            return Handicap.UNKNOWN;
        HandicapSeason hc =  getWinner().getHandicapSeasons().stream().filter(s->s.getSeason().equals(getSeason())).findFirst().orElse(null);
        if (hc == null) {return Handicap.UNKNOWN;}
        return hc.getHandicap();
    }

    public User getLoser() {
         if (homeRacks  == null || awayRacks == null)
            return playerAway;

        return homeRacks > awayRacks ? playerAway : playerHome;
    }

    public Integer getLoserRacks() {
        if (homeRacks  == null ||  awayRacks == null)
            return 0;

        return homeRacks > awayRacks ? awayRacks : homeRacks;
    }

    public Handicap getLoserHandicap() {
        if (getLoser() == null)
            return Handicap.UNKNOWN;

        HandicapSeason hc =  getLoser().getHandicapSeasons().stream().filter(s->s.getSeason().equals(getSeason())).findFirst().orElse(null);
        if (hc == null) {return Handicap.UNKNOWN;}
        return hc.getHandicap();
    }

    public boolean isWinner(User u) {
        return u != null && u.equals(getWinner());
    }

    /**
     * Is the loser home or away?
     * @return String
     */
    public String getLoserType() {
        if (teamMatch.getLoser().equals(teamMatch.getHome())) {
            return "home";
        }
        return "away";
    }

    /**
     * Is the winner home or away?
     * @return String
     */
    public String getWinnerType() {
        if (teamMatch.getWinner().equals(teamMatch.getHome())) {
            return "home";
        }
        return "away";
    }

    @JsonDeserialize(using = DateTimeDeSerializer.class)
    @JsonIgnore
    public LocalDateTime getMatchDate() {
        if (getTeamMatch() == null)
            return null;
        return getTeamMatch().getMatchDate();
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
    public String getDate() {
        if (getTeamMatch() == null)
            return null;
        return getTeamMatch().getMatchDate().toLocalDate().format(formatter);
    }

    public boolean hasUser(User u) {
        return u!= null && (u.equals(playerHome) || u.equals(playerAway) );
    }

    public boolean hasTeam(Team t) {
        return t!= null && (t.equals(getTeamMatch().getHome()) || t.equals(getTeamMatch().getAway()));
    }

    public Handicap getHandicap(User u) {
        if (u.equals(playerAway)) {
            return playerAwayHandicap ;
        }
        if (u.equals(playerHome)) {
            return playerHomeHandicap;
        }
        return Handicap.UNKNOWN;
    }

    public boolean isNine() {
        return getSeason().getDivision() == Division.NINE_BALL_TUESDAYS || getSeason().getDivision() == Division.NINE_BALL_CHALLENGE;
    }

    public void setReferenceTeam(Team referenceTeam) {
        this.referenceTeam = referenceTeam;
    }

    public User getOpponent() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerAway : playerHome;

        if (referenceUser != null)
            return referenceUser.equals(playerHome) ? playerAway : playerHome;

        return null;

    }


    public String getOpponentHandicap() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ?  Handicap.format(playerAwayHandicap):  Handicap.format(playerHomeHandicap);

        if (referenceUser != null)
            return referenceUser.equals(playerHome) ?  Handicap.format(playerAwayHandicap) : Handicap.format(playerHomeHandicap);

        return  Handicap.format(Handicap.UNKNOWN);
    }

    public User getOpponentPartner() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerAwayPartner : playerHomePartner;

        if (referenceUser != null)
            return referenceUser.equals(playerHome) ? playerAwayPartner : playerHomePartner;

        return null;
    }

    public Handicap getOpponentPartnerHandicap() {
         if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerAwayHandicapPartner : playerHomeHandicapPartner;

        if (referenceUser != null)
            return referenceUser.equals(playerHome) ? playerAwayHandicapPartner : playerHomeHandicapPartner;

        return Handicap.UNKNOWN;
    }

    public User getPartner() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerHomePartner : playerAwayPartner;

        if (referenceUser != null)
            return referenceUser.equals(playerHome) ? playerHomePartner : playerAwayPartner;

        return null;
    }

    public User getTeamMember() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerHome : playerAway;

        return referenceUser;
    }

    public boolean isWin() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? homeRacks > awayRacks : awayRacks > homeRacks;

        return referenceUser != null && isWinner(referenceUser);

    }
    public String getScore() {
        if (referenceTeam != null) {
            return referenceTeam.equals(teamMatch.getHome()) ? String.format("%s-%s", homeRacks, awayRacks) : String.format("%s-%s", awayRacks, homeRacks);
        }

        if (referenceUser != null) {
            return referenceUser.equals(playerHome) ? String.format("%s-%s", homeRacks, awayRacks) : String.format("%s-%s", awayRacks, homeRacks);
        }
        return String.format("%s-%s", homeRacks, awayRacks);
    }

    public Boolean isHill() {
        return Math.abs(homeRacks-awayRacks) == 1;
    }

    public String getRace() {
        return Handicap.race(playerAwayHandicap,playerHomeHandicap);
    }

    public String getTeamMemberHandicap() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? Handicap.format(playerHomeHandicap) : Handicap.format(playerAwayHandicap);

        if (referenceUser != null)
            return referenceUser.equals(playerHome) ? Handicap.format(playerHomeHandicap) : Handicap.format(playerAwayHandicap);

        return   Handicap.format(Handicap.UNKNOWN);
    }

    public Team getOpponentTeam() {
        if (referenceUser != null) {
            return teamMatch.getHome().getTeamMembers().getMembers().contains(referenceUser) ? teamMatch.getAway() : teamMatch.getHome();
        }

        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? teamMatch.getAway() : teamMatch.getHome();

        return null;
    }

    public Team getTeam() {
        if (referenceUser != null) {
            return teamMatch.getHome().getTeamMembers().getMembers().contains(referenceUser) ? teamMatch.getHome() : teamMatch.getAway();
        }
        return referenceTeam;
    }

    public Integer getTeamMemberRacks() {
        if (referenceUser != null) {
            return referenceUser.equals(playerHome) ?  homeRacks : awayRacks;
        }
        if (referenceTeam != null) {
            return referenceTeam.equals(teamMatch.getHome()) ? homeRacks : awayRacks;
        }

        return 0;
    }

    public Handicap getWinnerTeamHandicap() {
        return teamMatch.getWinner().hasUser(playerHome) ? playerHomeHandicap : playerAwayHandicap;
    }

    public Handicap getLoserTeamHandicap() {
        return teamMatch.getWinner().hasUser(playerHome) ? playerAwayHandicap : playerHomeHandicap;
    }

    public Integer getWinnerTeamRacks() {
        return teamMatch.getWinner().hasUser(playerHome) ? homeRacks : awayRacks;
    }

    public Integer getLoserTeamRacks() {
        return teamMatch.getWinner().hasUser(playerHome) ? awayRacks : homeRacks;
    }

    public User getWinnerTeamPlayer() {
        return teamMatch.getWinner().hasUser(playerHome) ? playerHome : playerAway;
    }

    public User getLoserTeamPlayer() {
        return teamMatch.getWinner().hasUser(playerHome) ? playerAway : playerHome;
    }

    public Integer getOpponentRacks() {
        if (referenceUser != null) {
            return referenceUser.equals(playerHome) ?  awayRacks : homeRacks;
        }
        if (referenceTeam != null) {
            return referenceTeam.equals(teamMatch.getHome()) ? awayRacks : homeRacks;
        }

        return 0;
    }

    public void setReferenceUser(User referenceUser) {
        this.referenceUser = referenceUser;
    }

    public void clearReference() {
        referenceTeam = null;
        referenceUser = null;
    }

    public String getResult() {
        if (isWin())
            return "W";

        return "L";
    }

    public MatchPoints getMatchPoints() {
        return matchPoints;
    }

    public void setMatchPoints(MatchPoints matchPoints) {
        this.matchPoints = matchPoints;
    }

    public static PlayerResult copy(PlayerResult result) {
        PlayerResult copy = new PlayerResult();
        ReflectionUtils.shallowCopyFieldState(result,copy);
        return copy;
    }

    public User getPlayerHomePartner() {
        return playerHomePartner;
    }

    public void setPlayerHomePartner(User playerHomePartner) {
        this.playerHomePartner = playerHomePartner;
    }

    public User getPlayerAwayPartner() {
        return playerAwayPartner;
    }

    public void setPlayerAwayPartner(User playerAwayPartner) {
        this.playerAwayPartner = playerAwayPartner;
    }

    public Handicap getPlayerHomeHandicapPartner() {
        return playerHomeHandicapPartner;
    }

    public void setPlayerHomeHandicapPartner(Handicap playerHomeHandicapPartner) {
        this.playerHomeHandicapPartner = playerHomeHandicapPartner;
    }

    public Handicap getPlayerAwayHandicapPartner() {
        return playerAwayHandicapPartner;
    }

    public void setPlayerAwayHandicapPartner(Handicap playerAwayHandicapPartner) {
        this.playerAwayHandicapPartner = playerAwayHandicapPartner;
    }


    @Override
    public String toString() {
        return "PlayerResult{" +
                "teamMatch=" + teamMatch +
                ", playerHome=" + playerHome +
                ", playerAway=" + playerAway +
                ", homeRacks=" + homeRacks +
                ", awayRacks=" + awayRacks +
                ", matchNumber=" + matchNumber +
                ", playerHomeHandicap=" + playerHomeHandicap +
                ", playerAwayHandicap=" + playerAwayHandicap +
                ", legacyId =" + getLegacyId() +
                ", id =" + getId() +
                '}';
    }
}
