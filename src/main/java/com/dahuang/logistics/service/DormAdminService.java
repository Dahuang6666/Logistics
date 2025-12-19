package com.dahuang.logistics.service;

import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.vo.DormChangeApplicationVO;

import java.util.List;

public interface DormAdminService {
    List<DormChangeApplicationVO> getAllApplication();

    boolean updateApplication(DormChangeApplication application);

    List<DormChangeApplicationVO> getApplicationList(String status);

    Object getAllRepairs();

    boolean updateStatus(Integer id, String status);

    boolean deleteRepair(Integer id);

    List<Announcement> getAllAnnouncements();

    boolean updateAnnouncement(Announcement announcement);

    boolean deleteAnnouncement(Integer id);

}
