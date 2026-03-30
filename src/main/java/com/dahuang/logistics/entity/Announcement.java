package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 公告实体类
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    /**
     * 公告主键ID
     */
    private Integer id;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 发布人
     */
    private String publisher;

    /**
     * 发布时间
     */
    private Date publishTime;

    /**
     * 删除标识
     * 0-未删除（默认），1-已删除
     */
    private Integer isDeleted;
    /**
     * 公告类型ID
     */
    private Integer announcementTypeId;
}