package com.society.leagues.api;


public abstract class ApiController {
    private static final String API_PATH = "/api/v1";

    public static final String AUTHENTICATE_URL = API_PATH + "/auth/login";
    public static final String ACCOUNT_URL = API_PATH + "/account";
    public static final String PLAYER_URL = API_PATH + "/player";
    public static final String SCHEDULER_URL = API_PATH + "/scheduler";
    public static final String DIVISION_URL = API_PATH + "/division";
    public static final String ADMIN_USER_URL = API_PATH + "/admin/user";

}
