package com.society.leagues.client.api.domain;

public class PlayerMatch {

    Player player;
    Player partner;
    Player opponent;
    Player partnerOpponent;

    Integer matchId;
    boolean won;

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public boolean isWon() {
        return won;
    }

    public void setWon(boolean won) {
        this.won = won;
    }

    public Player getPartner() {
        return partner;
    }

    public void setPartner(Player partner) {
        this.partner = partner;
    }

    public Integer getMatchId() {
        return matchId;
    }

    public void setMatchId(Integer matchId) {
        this.matchId = matchId;
    }

    public Player getOpponent() {
        return opponent;
    }

    public void setOpponent(Player opponent) {
        this.opponent = opponent;
    }

    public Player getPartnerOpponent() {
        return partnerOpponent;
    }

    public void setPartnerOpponent(Player partnerOpponent) {
        this.partnerOpponent = partnerOpponent;
    }
}
