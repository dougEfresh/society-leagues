package com.society.leagues.resource;

import com.society.leagues.client.api.admin.PlayerAdminApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.dao.PlayerAdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"ADMIN"})
@SuppressWarnings("unused")
public class PlayerAdminResource extends AdminApiResource implements PlayerAdminApi {
    @Autowired PlayerAdminDao dao;
    private static Logger logger = LoggerFactory.getLogger(PlayerAdminResource.class);

    @Override
    public Player create(Player player) {
        return dao.create(player);
    }

    @Override
    public Boolean delete(Player player) {
        return dao.delete(player);
    }

    @Override
    public Player modify(Player player) {
        return dao.modify(player);
    }
}
