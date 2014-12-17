package com.society.leagues.api.division;

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

@RequestMapping("/api/v1/division")
@RestController
@Api( value = "/division" ,
        description = "Division Stats & Info",
        basePath = "/api/vi",
        position = 4

)
@PreAuthorize("hasAuthority('ROLE_DOMAIN_USER')")
public class DivisionController extends ApiController {

    @Autowired DivisionDao dao;

    @RequestMapping(value = "/info", method = RequestMethod.POST)
    @ApiOperation(value = "/info", notes = "Returns Division Info (type,day..etc)")
    public List<Map<String,Object>> getInfo(@ApiParam(access = "internal") @CurrentlyLoggedInUser DomainUser domainUser,
                                            @ApiParam(required = false) @RequestParam(required = false) Integer divisionId) {

        if (divisionId == null || divisionId < 0) {
            return dao.getInfo(domainUser.getPlayer());
        }
        return dao.getInfo(divisionId);
    }

    @RequestMapping(value = "/standings", method = RequestMethod.POST)
    @ApiOperation(value = "/standings", notes = "Get Current Standings")
    public List<Map<String,Object>> getStandings(@ApiParam(access = "internal") @CurrentlyLoggedInUser DomainUser domainUser,
                                           @RequestParam Integer divisionId) {
        return dao.getStandings(divisionId);
    }

    @RequestMapping(value = "/list", method = RequestMethod.POST)
    @ApiOperation(value = "/list", notes = "Get all Divisions")
    public List<Map<String,Object>> getList(@ApiParam(access = "internal") @CurrentlyLoggedInUser DomainUser domainUser) {
        return dao.getList();
    }

    @RequestMapping(value = "/standingsWeek", method = RequestMethod.POST)
    @ApiOperation(value = "/standingsWeek", notes = "Display Results for a Week")
    public List<Map<String,Object>> getList(@ApiParam(access = "internal") @CurrentlyLoggedInUser DomainUser domainUser,
                                      @RequestParam Integer divisionId,
                                      @RequestParam Integer weekId) {
        return dao.getStandingsWeek(divisionId,weekId);
    }


}
