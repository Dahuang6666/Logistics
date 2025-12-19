package com.dahuang.logistics.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentDormitoryInfoDTO {
    private Integer userNo;
    private String buildingNumber;
    private String dormitoryNumber;
}
