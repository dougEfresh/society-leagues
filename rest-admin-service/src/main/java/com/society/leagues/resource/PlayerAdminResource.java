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
public class PlayerAdminResource extends AdminApiResource implements PlayerAdminApi {
    @Autowired PlayerAdminDao dao;
    private static Logger logger = LoggerFactory.getLogger(PlayerAdminResource.class);

    @Override
    public Player create(Player player) {
        if (!player.verify()) {
            logger.error("Could not verify player: " + player);
            return null;
        }
        Player created = dao.create(player);

        //TODO use the deny annotation instead
        if (created != null)
            created.setPassword(null);

        return created;
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
