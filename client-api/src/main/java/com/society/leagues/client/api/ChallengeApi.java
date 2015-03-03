package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.Challenge;
import com.society.leagues.client.api.domain.Player;
import com.society.leagues.client.api.domain.Slot;
import com.society.leagues.client.api.domain.User;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.List;

@Path(value = "/")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public interface ChallengeApi {
    
    @Path("/api/challenge/potential/{id}")
    @GET
    List<User> getPotentials(@PathParam(value = "id") Integer id);
    
    @POST
    @Path(value = "/api/challenge/request")
    Challenge requestChallenge(Challenge challenge);
    
    @POST
    @Path(value = "/api/challenge/accept")
    Challenge acceptChallenge(Challenge challenge);

    @GET
    @Path(value = "/api/challenge/list/{id}/")
    List<Challenge> listChallenges(@PathParam(value = "id") Integer userId);

    @POST
    @Path(value = "/api/challenge/cancel")
    Boolean cancelChallenge(Challenge challenge);

    @POST
    @Path(value = "/api/challenge/modify")
    Challenge modifyChallenge(Challenge challenge);
    
    @POST
    @Path(value = "/api/challenge/slots")
    List<Slot> slots(Date date);

    @RequestMapping(value = "/api/challenge/player", produces = MediaType.APPLICATION_ATOM_XML, method = RequestMethod.POST)
    List<Challenge> getByPlayer(Player p);
}
