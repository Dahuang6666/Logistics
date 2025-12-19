package com.dahuang.logistics.vo;
import com.dahuang.logistics.enums.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DormChangeApplicationVO {
    private String studentNo; //学号
    private Integer currentDormitoryId; //当前宿舍id
    private Integer targetDormitoryId;  //目标宿舍id
    private String reason;  //原因
    private ApplicationStatus status;
}
