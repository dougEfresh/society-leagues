package com.society.leagues.resource;

import com.society.leagues.api.domain.User;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.stereotype.Component;

import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Component
@Api( value = "/admin/user" ,
        description = "Player Management & Info",
        basePath = "/api/vi"
)
@Path("/api/admin/user")
public class UserResource extends ApiResource {

    @Path(value = "/create")
    @POST
    @ApiOperation(value = "/create" , notes = "Create a user")
    public Integer create(@ApiParam User user) {
        return 10;
    }
}