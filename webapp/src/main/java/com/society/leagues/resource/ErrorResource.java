package com.society.leagues.resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
public class ErrorResource {

    final static Logger logger = Logger.getLogger(ErrorResource.class);

    @ExceptionHandler(Exception.class)
    public void handleError(HttpServletRequest req, HttpServletResponse response, Exception exception) throws IOException {
        logger.error(exception.getMessage(),exception);
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED,"Unknown Error");
        response.flushBuffer();
    }
}
