package com.osc.sessionservice.response;

public class StatusCode {


    // Login status codes
    public static final int INVALID_USER_ID = 201;
    public static final int INVALID_PASSWORD = 202;
    public static final int ACTIVE_SESSION_EXISTS = 204;
    //    public static final int INVALID_CREDENTIALS_MAX_REACHED = 205;
    public static final int LOGIN_SUCCESS = 200;
    public static final int LOGIN_FAILURE = 0;
    public static final int UNEXPECTED_ERROR = 0;


    // Logout status codes
    public static final int LOGOUT_SUCCESS = 200;
    public static final int LOGOUT_FAILURE = 200;
}


