package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormChangeApplication {
    private Integer applicationId;
    private String studentNo;
    private Integer currentDormitoryId;
    private Integer targetDormitoryId;
    private String reason;
    private Date applicationTime; //申请时间
    private String status; // 待审批/已同意/已拒绝
    private Date approvalTime; //审核时间
    private String approverNo; //审核人工号
    private String comment; //留言
}