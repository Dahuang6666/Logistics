package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dormitory {
    private Integer dormitoryId;       // 主键ID
    private String dormitoryNo;        // 宿舍号
    private Integer buildingId;        // 宿舍楼ID(外键关联build表)
    private Integer capacity;          // 容量
    private Integer availableBeds;     // 剩余床位
    private String status;             // 状态:正常/维修中
}