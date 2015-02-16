package com.society.leagues.client.api;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;
import java.util.Map;

@Api(value = "/api/v1/account" ,
        description = "Account Information & Changes ",
        basePath = "/api/",
        position = 2
)
@Path(value = "/api/v1/account")
@Consumes(MediaType.APPLICATION_JSON)
public interface AccountApi {

    @Path(value = "/{id:[0-9].+}/info")
    @GET
    @ApiOperation(value = "/{id}/info", notes = "Returns account info for a league player")
    Map<String,Object> getAccount(@PathParam(value = "id") Integer id);
}
