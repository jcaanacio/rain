package com.sandari.rain.libraries.utils;

import com.sandari.rain.libraries.typings.enums.ErrorScope;
import com.sandari.rain.libraries.typings.interfaces.IErrorResponse;

public class ErrorResponse implements IErrorResponse {
    private String message;
    private int code;
    private ErrorScope errorScope;

    public ErrorResponse(String message, int code, ErrorScope errorScope) {
        this.message = message;
        this.code = code;
        this.errorScope = errorScope;
    }

    public int getCode() {
        return code;
    }

    public ErrorScope getErrorScope() {
        return errorScope;
    }

    public void setErrorScope(ErrorScope errorScope) {
        this.errorScope = errorScope;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
