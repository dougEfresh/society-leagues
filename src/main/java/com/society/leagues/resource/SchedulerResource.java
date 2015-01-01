package com.society.leagues.resource;

import com.society.leagues.dao.SchedulerDao;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;

@Path("/api/scheduler")
@RestController
@Api( value = "/scheduler" ,
        description = "Schedule Info & Management",
        basePath = "/api/vi",
        position = 4

)
public class SchedulerResource extends ApiResource {

    @Autowired SchedulerDao dao;

    @Path(value = "/list/{id:[0-9].+}")
    @GET
    @ApiOperation(value = "/list", notes = "Displays schedule for division id")
    public List<Map<String,Object>> getStandings(@PathParam(value = "id") Integer id) {
        return dao.getSchedule(id);
    }

}

