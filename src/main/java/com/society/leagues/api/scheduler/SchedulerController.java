package com.society.leagues.api.scheduler;

import com.society.leagues.api.ApiController;
import com.society.leagues.domain.DomainUser;
import com.society.leagues.domain.annotation.CurrentlyLoggedInUser;
import com.wordnik.swagger.annotations.Api;
import com.wordnik.swagger.annotations.ApiOperation;
import com.wordnik.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping("/api/v1/scheduler")
@RestController
@Api( value = "/scheduler" ,
        description = "Schedule Info and Management",
        basePath = "/api/vi",
        position = 4

)
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class SchedulerController extends ApiController {

    @Autowired SchedulerDao dao;

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "/list", notes = "Displays schedule for division id")
    public List<Map<String,Object>> getStandings(@ApiParam(access = "internal") @CurrentlyLoggedInUser DomainUser domainUser,
                                           @RequestParam int divisionId) {
        return dao.getSchedule(divisionId);
    }

}

