package com.society.leagues.model;

public class TimeModel {

    String time;
    boolean selected;

    public TimeModel() {
    }

    public TimeModel(String time, boolean selected) {
        this.time = time;
        this.selected = selected;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
