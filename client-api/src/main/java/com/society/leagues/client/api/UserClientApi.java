package com.society.leagues.client.api;

import com.society.leagues.client.api.domain.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.*;
import java.util.List;

public interface UserClientApi extends ClientApi<User> {
    
    @Override
    @GET
    @RequestMapping(value = "/api/client/users",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<User> get();

    @Override
    @RequestMapping(value = "/api/user/{id}",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    User get(@PathParam(value = "id") @PathVariable(value = "id") Integer id);

    @RequestMapping(value = "/api/user/{id}",method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    User get(@PathParam(value = "login") String login);

    @Override
    @RequestMapping(value = "/api/users/ids",method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    List<User> get(List<Integer> id);
}

