package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Build {
    private Integer id;                // 主键ID
    private String buildingNumber;     // 楼号
    private String assignedGender;     // 该楼对应的性别:男或女
    private String description;        // 备注信息
    private Date createTime;           // 创建时间
    private Date updateTime;           // 更新时间
    private String status;             //是否异常
}