package com.society.leagues.api;


public abstract class ApiController {
    private static final String API_PATH = "/api/v1";

    public static final String AUTHENTICATE_URL = API_PATH + "/auth/login";
    public static final String ACCOUNT_URL = API_PATH + "/account";
    public static final String PLAYER_URL = API_PATH + "/player";

}
