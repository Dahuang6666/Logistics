package com.dahuang.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDTO {
    private String userNo;
    private String password;
    private String username;
    private Integer role;
    private String phone;
    private String email;
    private String gender;
}