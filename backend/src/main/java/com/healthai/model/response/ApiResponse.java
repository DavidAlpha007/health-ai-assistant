package com.healthai.model.response;

import lombok.Data;

@Data
public class ApiResponse<T> {

    private int code;
    private String message;
    private T data;

    // Success response constructor
    public ApiResponse(T data) {
        this.code = 0;
        this.message = "success";
        this.data = data;
    }

    // Error response constructor
    public ApiResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.data = null;
    }

    // Static factory method for success response
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(data);
    }

    // Static factory method for error response
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message);
    }
}
