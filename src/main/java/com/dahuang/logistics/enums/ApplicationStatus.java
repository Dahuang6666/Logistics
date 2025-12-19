package com.dahuang.logistics.enums;

import lombok.Getter;

@Getter
public enum ApplicationStatus {
    PENDING("待审批"),
    APPROVED("已同意"),
    REJECTED("已拒绝");

    private final String desc;

    ApplicationStatus(String desc) {
        this.desc = desc;
    }
}