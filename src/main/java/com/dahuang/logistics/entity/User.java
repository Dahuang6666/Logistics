package com.dahuang.logistics.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private Integer id;
    private String userNo; // 学号/工号
    private String password;
    private String username;
    private Integer role; // 角色
    private String phone;
    private String email;
    private Integer status;
    private Date createTime;
    private String gender;
    private String avatarUrl;
}