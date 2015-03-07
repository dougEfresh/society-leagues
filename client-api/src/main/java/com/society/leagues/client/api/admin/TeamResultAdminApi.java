package com.society.leagues.client.api.admin;

import com.society.leagues.client.api.domain.TeamResult;
import org.springframework.web.bind.annotation.RequestBody;

public interface TeamResultAdminApi {

    TeamResult create(@RequestBody TeamResult teamResult);

    TeamResult modify(@RequestBody TeamResult teamResult);

}
