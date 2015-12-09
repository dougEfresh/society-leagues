package com.society.admin.resources;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Controller
public class BaseController {
    static Logger logger = LoggerFactory.getLogger(UserResource.class);
    @Autowired
    HttpServletRequest request;
    @Autowired
    HttpServletResponse response;
}
