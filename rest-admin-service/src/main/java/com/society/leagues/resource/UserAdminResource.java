package com.society.leagues.resource;

import com.society.leagues.client.api.admin.UserAdminApi;
import com.society.leagues.client.api.domain.User;
import com.society.leagues.dao.UserAdminDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.security.RolesAllowed;

@Component
@RolesAllowed(value = {"ADMIN","Operator"})
@SuppressWarnings("unused")
public class UserAdminResource extends AdminApiResource implements UserAdminApi {
    private static Logger logger = LoggerFactory.getLogger(UserAdminResource.class);

    @Autowired UserAdminDao dao;

    @Override
    public User create(User user) {
        if (!user.verify()) {
            logger.error("Could not verify user: " + user);
            return null;
        }
        User created = dao.create(user);

        //TODO use the deny annotation instead
        if (created != null)
            created.setPassword(null);

        return created;
    }

    @Override
    public Boolean delete(User user) {
        if (user == null || user.getId() == null) {
            logger.error("Invalid User: " + user);
            return Boolean.FALSE;
        }

        return dao.delete(user);
    }

    @Override
    public User modify(User user) {
        if (user == null || user.getId() == null) {
            logger.error("Invalid User: " + user);
            return null;
        }
        return dao.modify(user);
    }
}
