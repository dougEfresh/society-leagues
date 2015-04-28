package com.society.leagues.resource.client;


import com.society.leagues.adapters.PlayerAdapter;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.dao.PlayerDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RequestMapping(value = "/api")
@RestController
public class PlayerResource {

    @Autowired PlayerDao playerDao;

    @RequestMapping(value = "/players", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<Integer,PlayerAdapter> teams() {
        Map<Integer,PlayerAdapter> adapter = new HashMap<>();
        for (Player player: playerDao.get()) {
            adapter.put(player.getId(), new PlayerAdapter(player));
        }
        return adapter;
    }
}
