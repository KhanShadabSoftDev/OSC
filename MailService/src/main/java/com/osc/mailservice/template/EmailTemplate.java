package com.osc.mailservice.template;


public  class EmailTemplate {


    public static final String REGISTRATION_SUBJECT = "OSC Registration Credentials";
    public static final String REGISTRATION_BODY_TEMPLATE =
            "Dear User,\n\n" +
                    "Welcome to OSC! Here is your OTP code for registration:\n\n" +
                    "User Id:%s\n\n"+
                    "OTP Code: %s\n\n" +
                    "Please use this OTP to complete your registration process.\n\n" +
                    "If you did not request this OTP, please disregard this message.\n\n" +
                    "Best Regards,\n" +
                    "OSC Team";

    public static final String FORGOT_PASSWORD_SUBJECT = "OSC Password Reset Request";
    public static final String FORGOT_PASSWORD_BODY_TEMPLATE =
            "Dear User,\n\n" +
                    "We received a request to reset your password. Here is your OTP code:\n\n" +
                    "OTP Code: %s\n\n" +
                    "Please use this OTP to reset your password.\n\n" +
                    "If you did not request this password reset, please disregard this message.\n\n" +
                    "Best Regards,\n" +
                    "OSC Team";

}
