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
    public DaoListener userListener() {
        return new DaoListener(){
            @Override
            public void onAdd(LeagueObject object) {
                if (!(object instanceof User)) {
                    return ;
                }

            }

            @Override
            public void onChange(LeagueObject object) {
                if (!(object instanceof User)) {
                    return ;
                }
            }

            @Override
            public void onDelete(LeagueObject object) {
                if (!(object instanceof User)) {
                    return ;
                }
            }
        };
    }
}
