package com.society.leagues.resource;


import com.society.leagues.client.api.admin.MatchResultApi;
import com.society.leagues.client.api.domain.PlayerMatch;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"Root","Operator"})
public class MatchResultResource extends AdminApiResource implements MatchResultApi {

    @Override
    public Integer save(PlayerMatch matchResult) {
        return null;
    }

    @Override
    public Boolean delete(Integer id) {
        return true;
    }

    @Override
    public PlayerMatch get(Integer id) {
        return null;
    }
}
