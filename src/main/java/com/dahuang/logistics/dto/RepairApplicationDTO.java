package com.dahuang.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairApplicationDTO {
    private String userNo;
    private Integer dormitoryId;
    private String content;
    private String imageUrl;
    private String repairType;
    private String urgencyLevel;
}
