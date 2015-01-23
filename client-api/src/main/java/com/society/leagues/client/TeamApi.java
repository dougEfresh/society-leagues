package com.society.leagues.client;

import com.society.leagues.client.api.TeamClientApi;
import com.society.leagues.client.api.admin.TeamAdminApi;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface TeamApi extends TeamClientApi,TeamAdminApi {
    
}
