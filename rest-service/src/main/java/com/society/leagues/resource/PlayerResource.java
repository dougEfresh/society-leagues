package com.society.leagues.resource;

import com.society.leagues.dao.PlayerDao;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;

@Component
@Api( value = "/player" ,
      description = "Player Management & Info",
      basePath = "/api/vi"
)
@Path("/api/v1/player")
public class PlayerResource extends ApiResource {
    @Autowired PlayerDao dao;

    @Path(value = "/teamHistory/{id:[0-9].+}")
    @GET
    @ApiOperation(value = "/teamHistory" , notes = "Get players stats for all teams")
    public List<Map<String,Object>> teamHistory(@PathParam(value = "id") Integer id) {
        return dao.getTeamHistory(id);
    }
}
