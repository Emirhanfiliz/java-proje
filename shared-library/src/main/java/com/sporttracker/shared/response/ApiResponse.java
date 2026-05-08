package com.sporttracker.shared.response;

public class ApiResponse<T> {

    private boolean success;
    private String  message;
    private T       data;
    private int     statusCode;

    public ApiResponse() {}

    public ApiResponse(boolean success, String message, T data, int statusCode) {
        this.success    = success;
        this.message    = message;
        this.data       = data;
        this.statusCode = statusCode;
    }

    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(true, message, data, 200);
    }

    public static <T> ApiResponse<T> success(T data) {
        return success(data, "Operation successful");
    }

    public static <T> ApiResponse<T> error(String message, int statusCode) {
        return new ApiResponse<>(false, message, null, statusCode);
    }

    public boolean isSuccess()    { return success; }
    public String  getMessage()   { return message; }
    public T       getData()      { return data; }
    public int     getStatusCode(){ return statusCode; }

    public void setSuccess(boolean success)     { this.success    = success; }
    public void setMessage(String message)      { this.message    = message; }
    public void setData(T data)                 { this.data       = data; }
    public void setStatusCode(int statusCode)   { this.statusCode = statusCode; }
}
