package com.dahuang.logistics.vo;
import com.dahuang.logistics.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormChangeApplicationVO {
    private Integer applicationId; //申请ID
    private String studentNo; //学号
    private Integer currentDormitoryId; //当前宿舍id
    private Integer targetDormitoryId;  //目标宿舍id
    private String reason;  //原因
    private ApplicationStatus status;
    private Date applicationTime; //申请时间
    private String comment; //审批意见
    private String approverNo; //审批人账号
    private Date approvalTime; //审批时间
}
