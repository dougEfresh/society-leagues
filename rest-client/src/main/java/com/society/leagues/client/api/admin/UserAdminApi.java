package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.User;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface UserAdminApi {

    @Path(value = "api/admin/user/create")
    @POST
    User create(User user);

    @Path(value = "api/admin/user/delete")
    @POST
    Boolean delete(User user);

    @Path(value = "api/admin/user/modify")
    @POST
    User modify(User user);
}
