package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDormitoryInfo {
    private Integer id;
    private Integer userNo;
    private String buildingNumber;
    private String dormitoryNumber;
    private Date createTime;
    private Date updateTime;
    private Integer isInfoCompleted;
}