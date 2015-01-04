package com.society.leagues.resource;

import com.society.leagues.client.admin.api.PlayerAdminApi;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.dao.PlayerDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"Root","Operator"})
public class PlayerResource extends AdminApiResource implements PlayerAdminApi {
    @Autowired PlayerDao dao;
    private static Logger logger = LoggerFactory.getLogger(PlayerResource.class);

    @Override
    public Player create(Player player) {
        if (!player.verify()) {
            logger.error("Could not verify player: " + player);
            return null;
        }
        Player created =  dao.create(player);

        //TODO use the deny annotation instead
        if (created != null)
            created.setPassword(null);

        return created;
    }

    @Override
    public Boolean delete(Integer id) {
        return dao.delete(id);
    }

    @Override
    public Player modify(Player player) {
        return dao.modify(player);
    }
}
