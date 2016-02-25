package com.society.leagues.listener;

import com.society.leagues.client.api.domain.LeagueObject;

public interface DaoListener {

    void onAdd(LeagueObject object);

    void onChange(LeagueObject object);

    void onDelete(LeagueObject object);


}
