package com.dahuang.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id;
    private String userNo;
    private String password; // 存库时加密，查询时不返回真实密码
    private String username;
    private Integer role; // 1 学生 4 管理员
    private String phone;
    private String email;
    private Integer status; // 1 正常 0 禁用
    private String gender;  // '男' '女'
}
