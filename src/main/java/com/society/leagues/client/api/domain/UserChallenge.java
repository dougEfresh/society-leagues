package com.society.leagues.client.api.domain;

public class UserChallenge {
    User user;
    Player nineBallPlayer;
    Player eightBallPlayer;

    public UserChallenge(User user) {
        this.user = user;
    }

    public UserChallenge() {

    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Player getNineBallPlayer() {
        return nineBallPlayer;
    }

    public void setNineBallPlayer(Player nineBallPlayer) {
        this.nineBallPlayer = nineBallPlayer;
    }

    public Player getEightBallPlayer() {
        return eightBallPlayer;
    }

    public void setEightBallPlayer(Player eightBallPlayer) {
        this.eightBallPlayer = eightBallPlayer;
    }
}
