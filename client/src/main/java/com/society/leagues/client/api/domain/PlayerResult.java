package com.society.leagues.client.api.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.client.views.PlayerResultView;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.util.ReflectionUtils;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@SuppressWarnings("unused")
public class PlayerResult  extends LeagueObject {

    @JsonView(PlayerResultSummary.class) @NotNull @DBRef TeamMatch teamMatch;
    @JsonView(PlayerResultSummary.class) @NotNull @DBRef User playerHome;
    @JsonView(PlayerResultSummary.class) @DBRef User playerAway;
    @JsonView(PlayerResultSummary.class) @NotNull Integer homeRacks = 0;
    @JsonView(PlayerResultSummary.class) @NotNull Integer awayRacks = 0;
    @JsonView(PlayerResultSummary.class) @NotNull Integer matchNumber = 0;
    @JsonView(PlayerResultSummary.class) @NotNull Handicap playerHomeHandicap = Handicap.UNKNOWN;
    @JsonView(PlayerResultSummary.class) @NotNull Handicap playerAwayHandicap = Handicap.UNKNOWN;
    @JsonView(PlayerResultSummary.class) User playerHomePartner;
    @JsonView(PlayerResultSummary.class) User playerAwayPartner;
    @JsonView(PlayerResultSummary.class) Handicap playerHomeHandicapPartner;
    @JsonView(PlayerResultSummary.class) Handicap playerAwayHandicapPartner;
    @JsonView(PlayerResultSummary.class) @Transient MatchPoints matchPoints;
    @JsonView(PlayerResultSummary.class)  Boolean scotch = false;
    @JsonIgnore @Transient boolean forfeit = false;
    @JsonIgnore @Transient Boolean homeWinner = null;
    @JsonIgnore @Transient Boolean awayWinner = null;
    int homePoints = 0;
    int awayPoints = 0;
    Team referenceTeam = null;
    User referenceUser = null;

    public static  PlayerResult addForfeit(int matchNumber,TeamMatch tm) {
        PlayerResult result = new PlayerResult();
        result.setTeamMatch(tm);
        result.setMatchNumber(matchNumber);
        result.setId(matchNumber + "-forfeit");
        result.setPlayerAway(User.defaultUser());
        result.getPlayerAway().setFirstName("");
        result.getPlayerAway().setLastName("Forfeit");
        result.setPlayerHome(User.defaultUser());
        result.getPlayerHome().setFirstName("");
        result.getPlayerHome().setLastName("Forfeit");
        result.forfeit = true;
        return result;
    }

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


    public int getHomePoints() {
        return homePoints;
    }

    public void setHomePoints(int homePoints) {
        this.homePoints = homePoints;
    }

    public int getAwayPoints() {
        return awayPoints;
    }

    public void setAwayPoints(int awayPoints) {
        this.awayPoints = awayPoints;
    }

    public boolean isForfeit() {
        return forfeit;
    }

    public void setForfeit(boolean forfeit) {
        this.forfeit = forfeit;
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
        if (homeRacks == null)
            return 0;

        return homeRacks;
    }

    public void setHomeRacks(Integer homeRacks) {
        this.homeRacks = homeRacks;
    }

    public Integer getAwayRacks() {
        if (awayRacks == null)
            return 0;

        return awayRacks;
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

    public User getPartnerWinner() {
        if (homeRacks  == null || awayRacks == null)
            return playerHomePartner;

        return homeRacks > awayRacks ? playerHomePartner : playerAwayPartner;
    }

    @JsonIgnore
    public User getWinnerPartner() {
        return getPartnerWinner();
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


    public User getLoserPartner() {
         if (homeRacks  == null || awayRacks == null)
            return playerAwayPartner;

        return homeRacks > awayRacks ? playerAwayPartner : playerHomePartner;
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
        return u != null && ( u.equals(getWinner()) || u.equals(getPartnerWinner()));
    }

    /**
     * Is the loser home or away?
     * @return String
     */
    public String getLoserType() {
        if (teamMatch.getLoser() == null)
            return "";
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
        if (teamMatch.getWinner() == null)
            return "";

        if (teamMatch.getWinner().equals(teamMatch.getHome())) {
            return "home";
        }
        return "away";
    }

    @JsonDeserialize(using = DateTimeDeSerializer.class)
    @JsonView(PlayerResult.class)
    public LocalDateTime getMatchDate() {
        if (getTeamMatch() == null)
            return null;
        return getTeamMatch().getMatchDate();
    }

    static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d");
    public String getDate() {
        if (getTeamMatch() == null || getTeamMatch().getMatchDate() == null)
            return null;

        return getTeamMatch().getMatchDate().toLocalDate().format(formatter);
    }

    public boolean hasUser(User u) {
        return u!= null && (u.equals(playerHome) || u.equals(playerAway) || u.equals(playerHomePartner) || u.equals(playerAwayPartner));
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

    public Handicap getOpponentHandicap(User u) {
        if (u.equals(playerAway)) {
            return playerHomeHandicap ;
        }
        if (u.equals(playerHome)) {
            return playerAwayHandicap;
        }
        return Handicap.UNKNOWN;
    }

    public boolean isNine() {
        if (getSeason() == null)
            return false;

        return getSeason().getDivision() == Division.NINE_BALL_TUESDAYS || getSeason().getDivision() == Division.NINE_BALL_CHALLENGE;
    }

    public void setReferenceTeam(Team referenceTeam) {
        this.referenceTeam = referenceTeam;
    }

    public User getOpponent() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerAway : playerHome;

        if (referenceUser != null) {
            return referenceUser.equals(playerHome) || referenceUser.equals(playerHomePartner) ? playerAway : playerHome;
        }

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
            return referenceUser.equals(playerHome) || referenceUser.equals(playerHomePartner) ? playerAwayPartner : playerHomePartner;

        return null;
    }

    public String getGameType() {
        return teamMatch.getGameType();
    }

    public Handicap getOpponentPartnerHandicap() {
        if (getOpponentPartner() == null)
            return Handicap.NA;

        return getOpponentPartner().getHandicap(getSeason());
    }

    public User getPartner() {
        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? playerHomePartner : playerAwayPartner;

        if (referenceUser != null) {
            if (referenceUser.equals(playerAwayPartner)) {
                return playerAway;
            }
            if (referenceUser.equals(playerHomePartner)) {
                return playerHome;
            }

            return referenceUser.equals(playerHome) ? playerHomePartner : playerAwayPartner;
        }
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

        if (referenceUser != null) {
            if (referenceUser.equals(playerHomePartner)) {
                if (playerHomeHandicapPartner == null || playerHomeHandicapPartner == Handicap.UNKNOWN)
                    return Handicap.format(playerHomePartner.getHandicap(getSeason()));

                return Handicap.format(playerHomeHandicapPartner);
            }
            if (referenceUser.equals(playerAwayPartner)) {
                if (playerAwayHandicapPartner == null || playerAwayHandicapPartner == Handicap.UNKNOWN)
                    return Handicap.format(playerAwayPartner.getHandicap(getSeason()));

                return Handicap.format(playerAwayHandicapPartner);
            }
            return referenceUser.equals(playerHome) ? Handicap.format(playerHomeHandicap) : Handicap.format(playerAwayHandicap);
        }

        return  Handicap.format(Handicap.UNKNOWN);
    }

    public Team getOpponentTeam() {
        if (referenceUser != null) {
            return teamMatch.getHome().getMembers().getMembers().contains(referenceUser) ? teamMatch.getAway() : teamMatch.getHome();
        }

        if (referenceTeam != null)
            return referenceTeam.equals(teamMatch.getHome()) ? teamMatch.getAway() : teamMatch.getHome();

        return null;
    }

    public Team getTeam() {
        if (referenceUser != null) {
            return teamMatch.getHome().getMembers().getMembers().contains(referenceUser) ? teamMatch.getHome() : teamMatch.getAway();
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
        if (teamMatch.getWinner() == null)
            return Handicap.UNKNOWN;

        return teamMatch.getWinner().hasUser(playerHome) ? playerHomeHandicap : playerAwayHandicap;
    }

    public Handicap getLoserTeamHandicap() {
        if (teamMatch.getWinner() == null)
            return Handicap.UNKNOWN;
        return teamMatch.getWinner().hasUser(playerHome) ? playerAwayHandicap : playerHomeHandicap;
    }

    public Integer getWinnerTeamRacks() {
        if (teamMatch.getWinner() == null)
            return 0;

        return teamMatch.getWinner().hasUser(playerHome) ? homeRacks : awayRacks;
    }

    public Integer getLoserTeamRacks() {
        if (teamMatch.getWinner() == null)
            return 0;

        return teamMatch.getWinner().hasUser(playerHome) ? awayRacks : homeRacks;
    }

    public User getWinnerTeamPlayer() {
        if (teamMatch.getWinner() == null)
            return null;
        return teamMatch.getWinner().hasUser(playerHome) ? playerHome : playerAway;
    }


    public User getWinnerTeamPlayerPartner() {
        if (teamMatch.getWinner() == null)
            return null;

        if (playerHomePartner != null)
            return teamMatch.getWinner().hasUser(playerHomePartner) ? playerHomePartner : playerAwayPartner;

        if (playerAwayPartner != null)
            return teamMatch.getWinner().hasUser(playerAwayPartner) ? playerAwayPartner : playerHomePartner;

        return null;
    }


    public User getLoserTeamPlayerPartner() {
        if (teamMatch.getWinner() == null)
            return null;

        if (playerHomePartner != null)
            return teamMatch.getLoser().hasUser(playerHomePartner) ? playerHomePartner : playerAwayPartner;

        if (playerAwayPartner != null)
            return teamMatch.getLoser().hasUser(playerAwayPartner) ? playerAwayPartner : playerHomePartner;

        return null;
    }

    public User getLoserTeamPlayer() {
        if (teamMatch.getWinner() == null)
            return null;

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
        if (matchPoints == null)
            return new MatchPoints();

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


    public boolean isHomeWinner() {
        if (homeWinner != null)
            return homeWinner;

        return homeRacks > awayRacks;
    }

    public void setHomeWinner(boolean homeWinner) {
        this.homeWinner = homeWinner;
    }

    public boolean isAwayWinner() {
        if (awayWinner != null)
            return awayWinner;

        return awayRacks > homeRacks;
    }

    public void setAwayWinner(boolean awayWinner) {
        this.awayWinner = awayWinner;
    }

    public Integer getSetNumber() {
        if (matchNumber < 5)
            return 1;

        if (matchNumber >= 5 && matchNumber < 9)
            return 2;

        if (matchNumber >= 9 && matchNumber < 13)
            return 3;

        if (matchNumber >= 13 && matchNumber < 17)
            return 4;

        if (matchNumber >= 17 && matchNumber < 21)
            return 5;

        if (matchNumber >= 21 && matchNumber < 25)
            return 6;

        return 7;
    }

    public int getWinnerPoints() {
        if (referenceTeam == null)
            return homePoints;

        if (getWinnerType().equals("home")) {
            return homePoints;
        }
        return awayPoints;
    }

    public int getLoserPoints() {
        if (referenceTeam == null)
            return awayPoints;

        if (getWinnerType().equals("home")) {
            return awayPoints;
        }
        return homePoints;
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

    public boolean hasResults() {
        if (homeRacks == null || awayRacks == null)
            return false;

        return homeRacks + awayRacks > 0;
    }
}
