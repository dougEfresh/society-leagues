package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.views.PlayerResultSummary;

public class UserProfile {

    String profileUrl;
    @JsonView(PlayerResultSummary.class) String imageUrl;

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profileUrl='" + profileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
