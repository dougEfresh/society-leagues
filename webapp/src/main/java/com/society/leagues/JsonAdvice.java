package com.society.leagues;


import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractJsonpResponseBodyAdvice;

@ControllerAdvice
public class JsonAdvice extends AbstractJsonpResponseBodyAdvice {

    public JsonAdvice() {
        super("callback");
    }
}
