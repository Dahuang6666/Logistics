package com.dahuang.logistics.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Announcement {
    private Integer id;
    private String title;
    private String content;
    private String publisher;
    private Date publishTime;
    private Integer isDeleted;
}
