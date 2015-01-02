package com.society.leagues.domain.player;

import com.society.leagues.domain.interfaces.Player;

public class PlayerDb implements Player {

    int id;
    String login;
    String firstName;
    String lastName;
    boolean admin;

    public PlayerDb() {
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public String getLogin() {
        return login;
    }

    @Override
    public String getFirstName() {
        return firstName;
    }

    @Override
    public String getLastName() {
        return lastName;
    }

    public boolean isAdmin() {
      return admin;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }
}
