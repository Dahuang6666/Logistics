package com.dahuang.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private String userNo;
    private String password;
    private Integer role; // 1 学生 4 管理员
}