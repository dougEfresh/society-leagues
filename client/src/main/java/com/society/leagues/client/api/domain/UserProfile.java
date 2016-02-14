package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.society.leagues.client.views.PlayerResultSummary;
import com.society.leagues.converters.TimeDeSerializer;
import com.society.leagues.converters.TimeSerializer;
import org.springframework.cglib.core.Local;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class UserProfile {

    String profileUrl;
    @JsonView(PlayerResultSummary.class) String imageUrl;
    @JsonView(PlayerResultSummary.class) List<String> disabledSlots = new ArrayList<>();
    @JsonView(PlayerResultSummary.class) List<String> broadcastSlots = new ArrayList<>();
    @JsonView(PlayerResultSummary.class) boolean receiveBroadcasts;

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


    public boolean isReceiveBroadcasts() {
        return receiveBroadcasts;
    }

    public void setReceiveBroadcasts(boolean receiveBroadcasts) {
        this.receiveBroadcasts = receiveBroadcasts;
    }

    public List<String> getDisabledSlots() {
        return disabledSlots;
    }

    public void setDisabledSlots(List<String> disabledSlots) {
        this.disabledSlots = disabledSlots;
    }

    public void setDisabledSlotsLocalTime(List<LocalTime> disabledSlots) {
        setLocalTime(this.disabledSlots,disabledSlots);
    }

    public List<String> getBroadcastSlots() {
        return broadcastSlots;
    }

    public void setBroadcastSlots(List<String> broadcastSlots) {
        this.broadcastSlots = broadcastSlots;
    }

    public void setBroadcastSlotsLocalTime(List<LocalTime> broadcastSlots) {
        setLocalTime(this.broadcastSlots,broadcastSlots);
    }

    private void setLocalTime(List<String> t, List<LocalTime> times) {
        times.stream().filter(time->time != null).forEach(time->t.add(time.toString()));
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profileUrl='" + profileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
