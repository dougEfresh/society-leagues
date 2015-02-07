package com.society.leagues.resource;

import com.society.leagues.client.api.SeasonClientApi;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.SeasonDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.PathParam;
import java.util.List;

@Component
@SuppressWarnings("unused")
public class SeasonResource extends ApiResource implements SeasonClientApi {
    @Autowired SeasonDao dao;

    @Override
    public Season get(Integer id) {
        return dao.get(id);
    }

    @Override
    public List<Season> get() {
        return dao.get();
    }

    @Override
    public Season get(String name) {
        return dao.get(name);
    }

    @Override
    public List<Season> get(List<Integer> id) {
        return null;
    }
}
