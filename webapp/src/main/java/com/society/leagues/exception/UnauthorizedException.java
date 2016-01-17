package com.society.leagues.exception;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(int status, String msg) {
        super(status, msg);
    }
}
