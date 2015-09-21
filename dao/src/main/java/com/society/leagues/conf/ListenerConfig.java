package com.society.leagues.conf;

import com.society.leagues.Service.ResultService;
import com.society.leagues.Service.StatService;
import com.society.leagues.client.api.domain.LeagueObject;
import com.society.leagues.client.api.domain.PlayerResult;
import com.society.leagues.client.api.domain.TeamMatch;
import com.society.leagues.listener.DaoListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

//@Component
@SuppressWarnings("unused")
public class ListenerConfig {

    @Autowired ResultService resultService;
    @Autowired StatService statService;
    //@Value("${convert:false")
    boolean convert = true;

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

    //@Bean
    public DaoListener daoListener() {
        if (convert)
            return empty;

        return new DaoListener(){
            @Override
            public void onAdd(LeagueObject object) {
                if (object instanceof PlayerResult) {
                    resultService.refresh();
                    statService.refresh();
                    return;
                }
                if (object instanceof TeamMatch && ((TeamMatch) object).isHasResults()) {
                    statService.refresh();
                }
            }

            @Override
            public void onChange(LeagueObject object) {
                if (object instanceof PlayerResult) {
                    resultService.refresh();
                    statService.refresh();
                    return;
                }

                if (object instanceof TeamMatch && ((TeamMatch) object).isHasResults()) {
                    statService.refresh();
                }
            }

            @Override
            public void onDelete(LeagueObject object) {
                if (object instanceof PlayerResult) {
                    resultService.refresh();
                    statService.refresh();
                    return;
                }

                if (object instanceof TeamMatch && ((TeamMatch) object).isHasResults()) {
                    statService.refresh();
                }
            }
        };
    }
}
