package com.society.leagues.conf;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.listener.DaoListener;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@SuppressWarnings("unused")
public class ListenerConfig {

    public static DaoListener empty = new DaoListener() {
        @Override
        public void onAdd(LeagueObject object) {

        }

        @Override
        public void onChange(LeagueObject object) {

        }

        @Override
        public void onDelete(LeagueObject object) {

        }
    };

    @Bean
    public DaoListener statListener() {
        return empty;
    }

    @Bean
    public DaoListener userListener() {
        return empty;
    }
}
