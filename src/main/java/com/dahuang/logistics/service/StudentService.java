package com.dahuang.logistics.service;

import com.dahuang.logistics.dto.DormChangeApplicationDTO;
import com.dahuang.logistics.dto.RepairApplicationDTO;
import com.dahuang.logistics.entity.*;
import com.dahuang.logistics.vo.AnnouncementVO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface StudentService {
    int submitApplication(DormChangeApplicationDTO dormChangeApplicationDTO);

    boolean firstLoginInfoPage(String userNo);

    List<DormChangeApplication> getApplicationsByStudent(String studentNo);

    boolean createRepair(RepairApplicationDTO dto);

    List<RepairApplication> getRepairsByUser(String userNo);

    List<AnnouncementVO> getAnnouncementList(int pageNum, int pageSize,Integer priority,String searchKeyword);

    int getAnnouncementCount(Integer priority,String searchKeyword);

    Announcement getAnnouncementById(Integer id);


    int insertComplaint(ComplaintSuggestion cs);

    List<ComplaintSuggestion> getUserComplaints(String userNo);

    Result getAvailableDormsByBuilding(Integer buildingId);

    Result getAvailableBuildingsByGender(String userNo);

    Result submitFirstDormInfo(String userNo, Integer buildingId, String dormitoryNo);

    Result getStudentProfile(String userNo);

    String uploadAvatar(MultipartFile file);

    Result getDormitoryIdByUserNo(String userNo);
}
