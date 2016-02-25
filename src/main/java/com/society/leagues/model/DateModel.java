package com.society.leagues.model;


import java.time.LocalDate;

public class DateModel {

    LocalDate date;
    boolean selected;

    public DateModel(LocalDate date, boolean selected) {
        this.date = date;
        this.selected = selected;
    }

    public DateModel() {

    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
