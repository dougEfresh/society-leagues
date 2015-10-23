package com.society.leagues.client.api.domain;

public class UserProfile {

    String profileUrl;
    String imageUrl;

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
