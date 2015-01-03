package com.society.leagues.resource;


import com.society.leagues.client.admin.api.MatchResultApi;
import com.society.leagues.client.api.domain.PlayerMatch;
import org.springframework.stereotype.Component;

@Component
public class MatchResultResource extends ApiResource implements MatchResultApi {

    @Override
    public Integer save(PlayerMatch matchResult) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return null;
    }

    @Override
    public PlayerMatch get(Integer id) {
        return null;
    }
}
