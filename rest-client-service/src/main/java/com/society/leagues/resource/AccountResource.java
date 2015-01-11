package com.society.leagues.resource;

import com.society.leagues.client.api.AccountApi;
import com.society.leagues.dao.AccountDao;
import org.glassfish.jersey.server.monitoring.MonitoringStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Provider;
import java.util.Map;

@Component
public class AccountResource  implements AccountApi {
    @Inject
    Provider<MonitoringStatistics> monitoringStatisticsProvider;
    @Autowired AccountDao dao;

    @Override
    public Map<String,Object> getAccount(Integer id) {
        return dao.getAcctInfo(id);
    }
}
