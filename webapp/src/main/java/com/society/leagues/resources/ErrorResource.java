package com.society.leagues.resources;

import com.society.leagues.exception.UnauthorizedException;
import org.apache.log4j.Logger;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.multiaction.NoSuchRequestHandlingMethodException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Controller
@ControllerAdvice
public class ErrorResource  {

    final static Logger logger = Logger.getLogger(ErrorResource.class);
    static final Map<Class, HttpStatus> exceptionStatusCodeMap = new HashMap<>();
    
    static {
        exceptionStatusCodeMap.put(NoSuchRequestHandlingMethodException.class, HttpStatus.NOT_FOUND);
        exceptionStatusCodeMap.put(HttpRequestMethodNotSupportedException.class, HttpStatus.METHOD_NOT_ALLOWED);
        exceptionStatusCodeMap.put(HttpMediaTypeNotSupportedException.class, HttpStatus.UNSUPPORTED_MEDIA_TYPE);
        exceptionStatusCodeMap.put(HttpMediaTypeNotAcceptableException.class, HttpStatus.NOT_ACCEPTABLE);
        exceptionStatusCodeMap.put(MissingServletRequestParameterException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(ServletRequestBindingException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(ConversionNotSupportedException.class, HttpStatus.INTERNAL_SERVER_ERROR);
        exceptionStatusCodeMap.put(TypeMismatchException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(HttpMessageNotReadableException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(HttpMessageNotWritableException.class, HttpStatus.INTERNAL_SERVER_ERROR);
        exceptionStatusCodeMap.put(MethodArgumentNotValidException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(MissingServletRequestPartException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(BindException.class, HttpStatus.BAD_REQUEST);
        exceptionStatusCodeMap.put(NoHandlerFoundException.class, HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = {"/error"}, method = RequestMethod.GET)
    public ModelAndView error() {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("error","no error here");
        return mav;
    }

    @ExceptionHandler(value = {Exception.class, RuntimeException.class})
    public String handleError(Model model, HttpServletRequest req, HttpServletResponse response, Exception exception) throws IOException {
        if (exception.getCause() instanceof  UnauthorizedException || exception instanceof UnauthorizedException) {
            return "redirect:/app/login";
        }
        ModelAndView mav = new ModelAndView();
        if (model != null) {
            model.addAttribute("exception", exception);
            model.addAttribute("url", req.getRequestURL());
            model.addAttribute("error", exception.getMessage());
        }
        if (exceptionStatusCodeMap.containsKey(exception.getClass())) {
            response.setStatus(exceptionStatusCodeMap.get(exception.getClass()).value());
        } else {
            response.setStatus(HttpStatus.BAD_REQUEST.value());
        }
        logger.error(exception.getMessage(),exception);
        return "error";

    }
}
