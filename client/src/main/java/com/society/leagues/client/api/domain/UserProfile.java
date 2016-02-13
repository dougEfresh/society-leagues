package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.converters.TimeDeSerializer;
import com.society.leagues.converters.TimeSerializer;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    String profileUrl;
    @JsonView(PlayerResultSummary.class) String imageUrl;
    @JsonSerialize(using = TimeSerializer.class)
    @JsonDeserialize(using = TimeDeSerializer.class)
    List<LocalTime> disabledSlots = new ArrayList<>();
    List<LocalTime> broadcastSlots = new ArrayList<>();

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

    public List<LocalTime> getDisabledSlots() {
        return disabledSlots;
    }

    public void setDisabledSlots(List<LocalTime> disabledSlots) {
        this.disabledSlots = disabledSlots;
    }

    public List<LocalTime> getBroadcastSlots() {
        return broadcastSlots;
    }

    public void setBroadcastSlots(List<LocalTime> broadcastSlots) {
        this.broadcastSlots = broadcastSlots;
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profileUrl='" + profileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
