package com.dahuang.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationStatusRequest {
    private Integer applicationId;
    private String status;      // 传中文：待审批/已同意/已拒绝
    private String approverNo;  // 审核人工号
    private String comment;     // 审核意见
}
