package com.society.leagues.controller;

import com.society.leagues.dao.AccountDao;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.util.Map;

@Api( value = "/account" ,
        description = "Account Information & Changes ",
        basePath = "/api/",
        position = 2
)
@Component
@Path(value = "/api/account")
public class AccountController extends ApiController {

    @Autowired AccountDao dao;

    @Path(value = "/{id:[0-9].+}/info")
    @GET
    @ApiOperation(value = "/{id}/info", notes = "Returns account info for a league player")
    public Map<String,Object> getAccount(@PathParam(value = "id") Integer id) {
        return dao.getAcctInfo(id);
    }
}
