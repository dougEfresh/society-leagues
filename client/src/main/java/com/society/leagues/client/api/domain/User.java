package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.converters.DateTimeDeSerializer;
import com.society.leagues.converters.DateTimeSerializer;
import com.society.leagues.client.views.PlayerResultView;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.mapping.DBRef;
import sun.security.action.GetPropertyAction;

import javax.validation.constraints.NotNull;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.AccessController;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class User extends LeagueObject {

    @JsonView(PlayerResultSummary.class) @NotNull String firstName = "";
    @JsonView(PlayerResultSummary.class) @NotNull String lastName = "";
    @NotNull String email;
    String password;
    @NotNull String login;
    @JsonView(PlayerResultSummary.class) @NotNull Role role = Role.PLAYER;
    @JsonView(PlayerResultSummary.class) @NotNull Status status = Status.ACTIVE;
    @JsonView(PlayerResultSummary.class) String avatarHash;
    @JsonSerialize(using = DateTimeSerializer.class)
    @JsonDeserialize(using = DateTimeDeSerializer.class)
    LocalDateTime created = LocalDateTime.now();
    List<HandicapSeason> handicapSeasons = new ArrayList<>();
    List<TokenReset>  tokens = new ArrayList<>();

    Set<Team> currentTeams = new HashSet<>();

    @JsonView(PlayerResultSummary.class)
    UserProfile userProfile = new UserProfile();

    static String dftEncode = AccessController.doPrivileged(new GetPropertyAction("file.encoding"));
    static String defaultAvatarUrl = "/default";
    static {
        try {
            defaultAvatarUrl = URLEncoder.encode("https://leagues.societybilliards.com/app/img/default-avatar.png",dftEncode);
        } catch (UnsupportedEncodingException e) {
        }
    }

    public User() {
        this.created = LocalDateTime.now();
    }

    public User(String id) {this.id = id;}

    public static User defaultUser() {
        User u = new User();
        u.setId("-1");
        u.setFirstName("---");
        u.setLastName("---");
        return u;
    }

    public User(String firstName, String lastName, String password, String login, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.password = password;
        this.login = login;
        this.role = role;
    }

    public String getAvatarHash() {
        return avatarHash;
    }

    public void setAvatarHash(String avatarHash) {
        this.avatarHash = avatarHash;
    }

    public String getShortName() {
        if (firstName == null || lastName == null || lastName.length() == 0 || firstName.length() == 0)
            return "";
        return firstName + " " + lastName.substring(0,1) + ".";
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    @JsonIgnore
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

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public Role getRole() {
        return role == null ? Role.PLAYER : role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isAdmin() {
        return Role.isAdmin(role);
    }

    public String getName() {
        if (firstName == null || lastName == null)
        return "";

        return firstName + " " + lastName;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<HandicapSeason> getHandicapSeasons() {
        return handicapSeasons;
    }

    public void setHandicapSeasons(List<HandicapSeason> handicapSeasons) {
        this.handicapSeasons = handicapSeasons;
    }

    public List<HandicapSeason> getActiveHandicapSeasons() {
        List<HandicapSeason>  s = handicapSeasons != null ? handicapSeasons.stream().filter(hs->hs.getSeason().isActive()).collect(Collectors.toList()) : Collections.emptyList();
        s.sort(new Comparator<HandicapSeason>() {
            @Override
            public int compare(HandicapSeason o1, HandicapSeason o2) {
                return o1.getSeason().getDivision().order.compareTo(o2.getSeason().getDivision().order);
            }
        });
        return s;
    }

    public void addHandicap(HandicapSeason hc) {
        if (hc.getHandicap() != Handicap.UNKNOWN && hc.getHandicap() != Handicap.NA) {
            if (hc.getSeason().isNine() && !Handicap.isNine(hc.getHandicap())) {
                throw new RuntimeException("Adding " + hc.getHandicap() + " to " + hc.getSeason().getDisplayName());
            }
            if (!hc.getSeason().isNine() && Handicap.isNine(hc.getHandicap())) {
                throw new RuntimeException("Adding " + hc.getHandicap() + " to " + hc.getSeason().getDisplayName());
            }
        }
        if (!this.handicapSeasons.add(hc)) {
            HandicapSeason hs = this.handicapSeasons.stream().filter(h->h.getSeason().equals(hc.getSeason())).findFirst().orElse(null);
            if (hs != null)
                hs.setHandicap(hc.getHandicap());
        }
    }

     public void removeHandicap(HandicapSeason season) {
         Iterator<HandicapSeason> it = this.handicapSeasons.iterator();
         while(it.hasNext()) {
             Season s = it.next().getSeason();
             if (s.equals(season.getSeason())) {
                 it.remove();
             }
         }
    }

    public String getSheetName(String seasonId) {
        String n = getName() + " (";
        n += Handicap.format(getHandicap(new Season(seasonId))) + ")";
        return n;
    }

    public boolean isReal() {
        if (lastName == null)
            return false;
        return !(lastName.toLowerCase().contains("handicap") || lastName.toLowerCase().contains("forfeit") || getName().toLowerCase().trim().equals("bye"));
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public boolean isChallenge() {
        if (status != Status.ACTIVE)
            return false;
        if (handicapSeasons == null || handicapSeasons.isEmpty()) {
            return false;
        }
        return handicapSeasons.stream().filter(s->s.getSeason().getDivision().isChallenge()).count() > 0;
    }

    @JsonIgnore
    public List<Season> getSeasons() {
        return handicapSeasons.stream().map(HandicapSeason::getSeason).collect(Collectors.toList());
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
        return getHandicapSeasons().stream().filter(hs -> hs.getSeason().equals(s)).findFirst().orElse(HandicapSeason.UNKNOWN).getHandicap();
    }

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public boolean isProfile() {
        return userProfile != null && userProfile.getImageUrl() != null && userProfile.getProfileUrl() != null;
    }

    public boolean hasSeason(Season s) {
        if (handicapSeasons == null)
            return false;

        for (HandicapSeason handicapSeason : handicapSeasons) {
            if (handicapSeason.getSeason().equals(s))
                return true;
        }
        return false;
    }


    public Set<Team> getCurrentTeams() {
        return currentTeams;
    }

    public void setCurrentTeams(Set<Team> currentTeams) {
        this.currentTeams = currentTeams;
    }

    @JsonIgnore
    public String getAvatarUrl() {
        if (getUserProfile() == null || getUserProfile().getImageUrl() == null) {
            if (avatarHash != null) {
                return String.format("%s/%s?d=%s",
                        "https://www.gravatar.com/avatar",
                            avatarHash,
                            defaultAvatarUrl);
            }
            return "/app/img/default-avatar.png";
        }
        return getUserProfile().getImageUrl();
    }


    public static Comparator<User> sort = (o1, o2) -> o1.getFirstName().compareTo(o2.getFirstName());

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
