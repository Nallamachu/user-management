package com.msrts.hostel.exception;

public class ErrorConstants {
    public static final String RUNTIME_EXCEPTION = "Runtime Exception. Contact Support Team.";
    public static final String OTP_GENERATION_EXCEPTION = "Exception occurred while generating OTP and sending email.";
    public static final String INVALID_OTP = "Given OTP is not valid.";
    public static final String ERROR_WRONG_PASSWORD = "Wrong Password";
    public static final String ERROR_PASSWORDS_NOT_MATCHED = "Password are not the same";
    public static final String INVALID_REQUEST = "Request is not valid";
    public static final String INVALID_INPUT_ID = "Given input Id is not valid";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid user credentials. Wrong username/password.";
    public static final String ERROR_USER_EXISTS = "User already exists with given username or mobile";
    public static final String ERROR_EMAIL_EXISTS = "Email already exists, kindly try with new email or login";
    public static final String ERROR_EMAIL_IS_NOT_VALID = "Given email id is not valid";
    public static final String ERROR_MOBILE_NO_EXISTS = "Mobile number already exists, kindly try with new mobile no or login";
    public static final String ERROR_MOBILE_NOT_VALID = "Given mobile number is not valid";
    public static final String ERROR_USER_NOT_FOUND = "No user found for given user-id/username";
    public static final String ERROR_USER_NOT_FOUND_GIVEN_MOBILE = "No user found for given mobile no \s";
    public static final String ERROR_REGISTER_SHOULD_NOT_BE_NULL = "Registration request should not be null";
    public static final String ERROR_HOSTEL_NOT_FOUND = "Hostel does not exits with given id";
    public static final String ERROR_ROOM_NOT_FOUND = "Room does not exits with given id";
    public static final String ERROR_ROOM_EXISTS = "Room already exits with given room no ";
    public static final String ERROR_ROOM_NOT_EMPTY = "Can not delete room due to room is not empty";
    public static final String ERROR_ROOM_IS_FULL = "Selected room is not valid or Room is occupied fully";
    public static final String ERROR_TENANT_NOT_FOUND = "Tenant does not exists with given id ";
    public static final String ERROR_TENANT_NOT_SAVED = "Error while saving tenant from payment ";
    public static final String ERROR_NAME_OR_ID_NUMBER_MANDATORY = "Either Name or Government ID number is mantory";
    public static final String ERROR_PAYMENT_NOT_FOUND = "Payment does not exists with given id ";
    public static final String ERROR_EXPENSE_NOT_FOUND = "Expense does not exists with given id ";
    public static final String ERROR_PARTNER_GROUP_NOT_FOUND = "Partner group does not exists with given partner id ";
    // Expense Related
    public static final String INVALID_AMOUNT = "Given amount is not valid";
    public static final String TIME_PERIOD_LAST_MONTH = "LAST_MONTH";
    public static final String TIME_PERIOD_CURRENT_MONTH = "CURRENT_MONTH";
    public static final String INVALID_TIME_PERIOD = "Invalid Time period. Time period should be LAST_MONTH/CURRENT_MONTH.";

    public static final String ERROR_FILE_UPLOAD = "Exception occurred while uploading the file. File size should not exceed 1 MB.";

    //REPORTS
    public static final String ERROR_INCOME_REPORT = "Error while generating income report. ";
    public static final String ERROR_EXPENSE_REPORT = "Error while generating income report. ";
}
