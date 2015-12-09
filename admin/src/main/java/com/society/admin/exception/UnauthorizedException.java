package com.society.admin.exception;

public class UnauthorizedException extends ApiException {

    public UnauthorizedException(int status, String msg) {
        super(status, msg);
    }
}
