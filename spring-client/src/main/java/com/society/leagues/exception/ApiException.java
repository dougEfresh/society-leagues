package com.society.leagues.exception;

public class ApiException extends Exception {

    private int status;
    private String msg;

    public ApiException(int status, String msg){
        this.status = status;
        this.msg = msg;
    }
}
