package com.dahuang.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormChangeApplicationDTO {
    private String studentNo; //学号
    private Integer currentDormitoryId; //当前宿舍id
    private String reason;  //原因
}