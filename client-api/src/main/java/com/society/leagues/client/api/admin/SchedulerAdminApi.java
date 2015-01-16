package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.Match;
import com.society.leagues.client.api.domain.Season;
import com.society.leagues.client.api.domain.Team;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface SchedulerAdminApi {

    @Path("api/admin/scheduler/create/{id}")
    @POST
    List<Match> create(@PathParam(value = "id") final Integer id, final List<Team> teams);

    @Path("api/admin/scheduler/create")
    @POST
    Match create(Match match);
    
}
