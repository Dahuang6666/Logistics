package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairApplication {
    private Integer id;
    private String userNo;
    private Integer dormitoryId;
    private String content;
    private String imageUrl;
    private String status;      // 默认：待处理
    private Date createTime;
    private Date updateTime;
    private Integer isDeleted;// 0 - 正常，1 - 已删除
    private String repairType;
    private String urgencyLevel;
}
