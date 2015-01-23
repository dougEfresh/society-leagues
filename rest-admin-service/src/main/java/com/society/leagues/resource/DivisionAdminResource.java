package com.society.leagues.resource;

import com.society.leagues.client.api.admin.DivisionAdminApi;
import com.society.leagues.client.api.domain.division.Division;
import com.society.leagues.dao.DivisionAdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"ADMIN"})
public class DivisionAdminResource extends AdminApiResource implements DivisionAdminApi {

    private static Logger logger = LoggerFactory.getLogger(DivisionAdminResource.class);
    @Autowired DivisionAdminDao dao;

    @Override
    public Division create(Division division) {
        if (division == null || division.getType() == null || division.getLeague() == null) {
            logger.error("Division is not verified: "+ division);
            return null;
        }
        return dao.create(division);
    }

    @Override
    public Boolean delete(Division division) {
        if (division == null || division.getId() == null) {
            logger.error("Division is not verified: "+ division);
            return Boolean.FALSE;
        }

        return dao.delete(division);
    }

    @Override
    public Division modify(Division division) {
         if (division == null || division.getType() == null || division.getLeague() == null || division.getId() == null) {
             logger.error("Division is not verified: "+ division);
            return null;
        }

        return dao.modify(division);
    }
}
