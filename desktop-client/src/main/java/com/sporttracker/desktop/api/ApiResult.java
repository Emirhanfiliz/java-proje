package com.sporttracker.desktop.api;

public class ApiResult<T> {

    private final boolean success;
    private final String  message;
    private final T       data;
    private final int     statusCode;

    public ApiResult(boolean success, String message, T data, int statusCode) {
        this.success    = success;
        this.message    = message;
        this.data       = data;
        this.statusCode = statusCode;
    }

    public boolean isSuccess()  { return success; }
    public String  getMessage() { return message; }
    public T       getData()    { return data; }
    public int     getStatusCode() { return statusCode; }
}
