package com.society.admin.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseController {
    static Logger logger = LoggerFactory.getLogger(UserResource.class);

    @ModelAttribute
    public void setCookieHeader(HttpServletResponse response) {

    }

}
