package com.society.leagues.client.api.domain;

import com.fasterxml.jackson.annotation.JsonView;
import com.society.leagues.client.views.PlayerResultSummary;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class UserProfile {

    String profileUrl;
    @JsonView(PlayerResultSummary.class) String imageUrl;
    @JsonView(PlayerResultSummary.class) List<String> disabledSlots = new ArrayList<>();
    @JsonView(PlayerResultSummary.class) List<String> broadcastSlots = new ArrayList<>();
    @JsonView(PlayerResultSummary.class) boolean receiveBroadcasts;
    @JsonView(PlayerResultSummary.class) List<String> blockedDates = new ArrayList<>();

    public List<String> getBlockedDates() {
        return blockedDates;
    }

    public void setBlockedDates(List<String> blockedDates) {
        if (blockedDates == null)
            this.blockedDates = new ArrayList<>();
        else
            this.blockedDates = blockedDates;
    }

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
        if (disabledSlots == null)
            this.disabledSlots = Collections.emptyList();
        else
            this.disabledSlots = disabledSlots;
    }

    public void setDisabledSlotsLocalTime(List<LocalTime> disabledSlots) {
        setLocalTime(this.disabledSlots,disabledSlots);
    }

    public List<String> getBroadcastSlots() {
        return broadcastSlots;
    }

    public void setBroadcastSlots(List<String> broadcastSlots) {
        if (broadcastSlots == null)
            this.broadcastSlots = Collections.emptyList();
        else
            this.broadcastSlots = broadcastSlots;
    }

    public void setBroadcastSlotsLocalTime(List<LocalTime> broadcastSlots) {
        setLocalTime(this.broadcastSlots,broadcastSlots);
    }

    private void setLocalTime(List<String> t, List<LocalTime> times) {
        if (times == null)
            return;
        times.stream().filter(time->time != null).forEach(time->t.add(time.toString()));
    }

    @Override
    public String toString() {
        return "UserProfile{" +
                "profileUrl='" + profileUrl + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }

    public boolean hasBlockedDate(String date) {
        return blockedDates.contains(date);
    }

    public boolean hasBlockedTime(Slot s) {
        return disabledSlots.contains(s.getTime());
    }
}
