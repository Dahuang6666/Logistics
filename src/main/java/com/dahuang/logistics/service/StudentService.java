package com.dahuang.logistics.service;

import com.dahuang.logistics.dto.DormChangeApplicationDTO;
import com.dahuang.logistics.dto.RepairApplicationDTO;
import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.ComplaintSuggestion;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.RepairApplication;

import java.util.List;

public interface StudentService {
    int submitApplication(DormChangeApplicationDTO dormChangeApplicationDTO);

    boolean firstLoginInfoPage(String userNo);

    List<DormChangeApplication> getApplicationsByStudent(String studentNo);

    boolean createRepair(RepairApplicationDTO dto);

    List<RepairApplication> getRepairsByUser(String userNo);

    List<Announcement> getAnnouncementList(int pageNum, int pageSize);

    int getAnnouncementCount();

    Announcement getAnnouncementById(Integer id);


    int insertComplaint(ComplaintSuggestion cs);

    List<ComplaintSuggestion> getUserComplaints(String userNo);
}
