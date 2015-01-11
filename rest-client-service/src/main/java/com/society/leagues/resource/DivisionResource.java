package com.society.leagues.resource;

import com.society.leagues.client.api.DivisionClientApi;
import com.society.leagues.client.api.domain.division.Division;
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

@Component
@SuppressWarnings("unused")
public class DivisionResource extends ApiResource  implements DivisionClientApi {

    @Autowired DivisionDao dao;

    @Override
    public List<Division> list() {
        return dao.list();
    }
}
