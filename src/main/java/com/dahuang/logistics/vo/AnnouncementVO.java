package com.dahuang.logistics.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnnouncementVO {
    private Integer id;
    private String title;
    private String content;
    private String publisher;
    private Date   publishTime;
    private Integer isDeleted;
    private Integer announcementTypeId;//公告类型Id
    private String  announcementTypeName;
    private Integer priority;//优先级
}
