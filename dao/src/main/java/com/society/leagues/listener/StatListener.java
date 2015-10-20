package com.society.leagues.listener;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.service.StatService;
import org.springframework.stereotype.Component;

@Component
public class StatListener implements DaoListener {
    StatService statService;

    public StatListener() {
    }

    @Override
    public void onAdd(LeagueObject object) {
        if (statService != null)
            statService.refresh();
    }

    @Override
    public void onChange(LeagueObject object) {
        if (statService != null)
            statService.refresh();
    }

    @Override
    public void onDelete(LeagueObject object) {
        if (statService != null)
            statService.refresh();
    }

    public void setStatService(StatService statService) {
        this.statService = statService;
    }
}
