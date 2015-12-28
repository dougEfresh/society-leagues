package com.society.admin.model;

import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.client.api.domain.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class TeamMatchModel {

    List<TeamMatch> matches = new ArrayList<>();

    public TeamMatchModel(List<TeamMatch> matches) {
        matches.sort(new Comparator<TeamMatch>() {
            @Override
            public int compare(TeamMatch o1, TeamMatch o2) {
                return o1.getMatchDate().compareTo(o2.getMatchDate());
            }
        });
        this.matches = matches;
    }

    public TeamMatchModel() {
    }

    public List<TeamMatch> getMatches() {

        return matches;
    }

    public void setMatches(List<TeamMatch> matches) {
        this.matches = matches;
    }

    public Season getSeason() {
        return matches.iterator().next().getSeason();
    }

    /**
     * Score sheets
     * @param index
     * @return
     */
    public String getEightFrontMargin(Integer index) {
        if (index == 0) {
            return "";
        }

        if (index == 1) {
            return "margin-top-neg-10";
        }

        if (index == 2) {
            return "margin-top-neg-8";
        }

        if (index == 3) {
            return "margin-top-neg-11";
        }

        if (index == 4) {
            return "margin-top-neg-7";
        }

        if (index == 5) {
            return "margin-top-neg-10";
        }

        if (index == 6) {
            return "margin-top-neg-8";
        }

        if (index == 7) {
            return "margin-top-neg-10";
        }
        return "";
    }

    public String getEightBackMargin(Integer index) {
         if (index == 0) {
             return "";
         }

         if (index == 1) {
             return "margin-top-neg-9";
         }

         if (index == 2) {
             return "margin-top-neg-8";
         }

         if (index == 3 || index == 4) {
             return "";
         }
         if (index == 5) {
             return "margin-top-neg-7";
         }

         if (index == 6) {
             return "margin-top-neg-11";
         }

         if (index == 7) {
             return "margin-top-clear";
         }

         return "";
    }

    public String getFrontMargin(Integer  index) {
        if (getSeason().isEight() || getSeason().isScramble())
            return getEightFrontMargin(index);

        return "";
    }

    public String getBackMargin(Integer index) {
        if (getSeason().isEight() || getSeason().isScramble())
            return getEightBackMargin(index);

        return "";
    }

}
