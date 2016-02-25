package com.society.leagues.conf.spring;

import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.listener.DaoListener;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
    ThreadPoolTaskExecutor threadPoolExecutor() {
        ThreadPoolTaskExecutor threadPoolExecutor = new ThreadPoolTaskExecutor();
        threadPoolExecutor.setCorePoolSize(1);
        return threadPoolExecutor;
    }
    @Bean
    public DaoListener userListener() {
        return empty;
    }
}
