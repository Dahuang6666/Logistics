package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RepairAnalysisResult {
    private String type; //类型
    private String urgency; // 紧急度

}
