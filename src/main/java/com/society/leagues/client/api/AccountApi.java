package com.society.leagues.client.api;

import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;

@Api(value = "/account" ,
        description = "Account Information & Changes ",
        basePath = "/api/",
        position = 2
)
@Path(value = "/api/account")
public interface AccountApi {

    @Path(value = "/{id:[0-9].+}/info")
    @GET
    @RolesAllowed({"user"})
    @ApiOperation(value = "/{id}/info", notes = "Returns account info for a league player")
    Map<String,Object> getAccount(@PathParam(value = "id") Integer id);
}
