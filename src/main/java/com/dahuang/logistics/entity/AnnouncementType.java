package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementType {
    /**
     * 类型ID
     */
    private Integer id;

    /**
     * 类型名称
     */
    private String typeName;

    /**
     * 优先级(三级优先级，1,2,3（最高级）)
     */
    private Integer priority;

    /**
     * 创建时间
     */
    private Date createdDate;
}