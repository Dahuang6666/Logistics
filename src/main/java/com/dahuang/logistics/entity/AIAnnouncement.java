package com.dahuang.logistics.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AIAnnouncement {

    private String topic;        // 主题
    private String publisher;   // 发布单位/人
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date publishDate;  // 发布日期（如 2025-07-11 14:00:00）
    private String summary;       //简单信息
}
