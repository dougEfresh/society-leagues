package com.society.leagues.resource;

import com.society.leagues.dao.DivisionDao;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.List;
import java.util.Map;

@Path("/api/division")
@Api( value = "/division" ,
        description = "Division Stats & Info",
        basePath = "/api/vi",
        position = 4

)
@Component
public class DivisionResource extends ApiResource {

    @Autowired DivisionDao dao;

    @Path(value = "/{id:[0-9].+}/info")
    @GET
    @ApiOperation(value = "/{id:[0-9].+}/info", notes = "Returns Division Info (type, day..etc)")
    public List<Map<String,Object>> getInfo(@ApiParam @PathParam(value = "id") Integer id) {
        return dao.getInfo(id);
    }

    @Path(value = "/{id:[0-9].+}:standings")
    @GET
    @ApiOperation(value = "/standings", notes = "Get Current Standings")
    public List<Map<String,Object>> getStandings(@PathParam(value = "id") Integer id) {
        return dao.getStandings(id);
    }

    @Path(value = "/list")
    @GET
    @ApiOperation(value = "/list", notes = "Get all Divisions")
    public List<Map<String,Object>> getList() {
        return dao.getList();
    }

    @Path(value = "/standingsWeek/{divisionId:[0-9].+}/{weekId:[0-9].+}")
    @ApiOperation(value = "/standingsWeek", notes = "Display Results for a Week")
    public List<Map<String,Object>> getStandingsWeek(
                                            @PathParam(value = "id") Integer divisionId,
                                            @PathParam(value = "weekId") Integer weekId) {
        return dao.getStandingsWeek(divisionId,weekId);
    }
}
