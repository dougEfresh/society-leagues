package com.society.admin.resources;

import com.society.admin.exception.UnauthorizedException;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@ControllerAdvice
public class ErrorResource {

    final static Logger logger = Logger.getLogger(ErrorResource.class);

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public String handleError(HttpServletRequest req, HttpServletResponse response, Exception exception) throws IOException {
        if (exception.getCause() instanceof  UnauthorizedException) {
            ModelAndView mav = new ModelAndView();
            mav.addObject("error","Unauthorized");
            mav.setViewName("login");
            return "login";
        }
        ModelAndView mav = new ModelAndView();
        //mav.addObject("exception", exception);
        //mav.addObject("url", req.getRequestURL());
        mav.setViewName("error");
        return "error";
        //response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unknown Error");
        //response.flushBuffer();
    }
}
