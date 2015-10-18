package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import com.society.leagues.client.views.PlayerResultView;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class User extends LeagueObject {

    @NotNull String firstName;
    @NotNull String lastName;
    @NotNull String email;
    String password;
    @NotNull String login;
    @NotNull Role role = Role.PLAYER;
    @NotNull Status status = Status.ACTIVE;

    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created;
    Set<HandicapSeason> handicapSeasons = new HashSet<>();
    List<TokenReset>  tokens = new ArrayList<>();

    public User() {
        this.created = LocalDateTime.now();
    }

    public User(String id) {this.id = id;}

    public static User defaultUser() {
        User u = new User();
        u.setId("0");
        return u;
    }

    public User(String firstName, String lastName, String password, String login, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.login = login;
        this.role = role;
    }

    @JsonView(PlayerResultView.class)
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    @JsonView(PlayerResultView.class)
    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @JsonIgnore
    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    @JsonView(PlayerResultView.class)
    public Role getRole() {
        return role == null ? Role.PLAYER : role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return Role.isAdmin(role);
    }

    @JsonView(PlayerResultView.class)
    public String getName() {
        return firstName + " " + lastName;
    }

    @JsonView(PlayerResultView.class)
    public Status getStatus() {
        return status;
    }

    public void setHandicapSeasons(Set<HandicapSeason> handicapSeasons) {
        this.handicapSeasons = handicapSeasons;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @JsonView(PlayerResultView.class)
    public Set<HandicapSeason> getHandicapSeasons() {
        return handicapSeasons;
    }

    public void addHandicap(HandicapSeason hc) {
        if (hc.getSeason().isNine() && !Handicap.isNine(hc.getHandicap())) {
            throw new RuntimeException("Adding " + hc.getHandicap() + " to "+ hc.getSeason().getDisplayName());
        }
        if (!hc.getSeason().isNine() && Handicap.isNine(hc.getHandicap())) {
            throw new RuntimeException("Adding " + hc.getHandicap() + " to " + hc.getSeason().getDisplayName());
        }
        this.handicapSeasons.add(hc);
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @JsonView(PlayerResultView.class)
    public boolean isChallenge() {
        if (status != Status.ACTIVE)
            return false;

        return handicapSeasons.stream().filter(s->s.getSeason().getDivision().isChallenge()).count() > 0;
    }

    public List<Season> getSeasons() {
        List<Season> list =handicapSeasons.stream().map(HandicapSeason::getSeason).collect(Collectors.toList());
        list.sort(Season.sort);
        return list;
    }

    public boolean hasSameSeason(User u) {
        for (HandicapSeason u1 : handicapSeasons) {
            for (HandicapSeason u2: u.getHandicapSeasons()) {
                if (u1.getSeason().equals(u2.getSeason())) {
                    return true;
                }
            }
        }
        return false;
    }

    @JsonIgnore
    public boolean isFake() {
        return false;
        //return lastName.toLowerCase().contains("handicap") || lastName.toLowerCase().contains("forfeit");
    }

    @JsonIgnore
    public List<TokenReset> getTokens() {
        return tokens;
    }

    public void setTokens(List<TokenReset> tokens) {
        this.tokens = tokens;
    }

    public boolean isActive() {
        return getSeasons().stream().filter(Season::isActive).findFirst().isPresent();
    }

    public Handicap getHandicap(Season s) {
        getHandicapSeasons().stream().filter(hs->hs.getSeason().equals(s)).findFirst().orElse(HandicapSeason.UNKNOWN).getHandicap();
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", login='" + login + '\'' +
                ", role=" + role +
                ", status=" + status +
                ", created=" + created +
                ", handicapSeasons=" + handicapSeasons +
                '}';
    }
}
