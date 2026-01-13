package com.healthai.model.request;

import lombok.Data;

@Data
public class LoginRequest {

    private String phone;
    private String code;
}
