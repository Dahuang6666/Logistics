package com.dahuang.logistics.mapper;

import com.dahuang.logistics.dto.DormChangeApplicationDTO;
import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.ComplaintSuggestion;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.RepairApplication;
import com.dahuang.logistics.vo.AnnouncementVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface StudentMapper {
    //学生提交换宿申请
    Integer submitApplication(DormChangeApplication dormChangeApplication);
     //是否第一次登录
    @Select("select is_info_completed from school_backend_manage.student_dormitory_info where user_no=#{userNo}")
    Integer firstLoginInfoPage(String userNo);
    //查看换宿舍申请
    @Select("SELECT * FROM school_backend_manage.dorm_change_application WHERE student_no = #{studentNo} ORDER BY application_time DESC")
    List<DormChangeApplication> getApplicationsByStudent(String studentNo);

    //维修
    @Insert("INSERT INTO school_backend_manage.repair_application " +
            "(user_no, dormitory_id, content, image_url, status, create_time, update_time, is_deleted, repair_type, urgency_level) " +
            "VALUES (#{userNo}, #{dormitoryId}, #{content}, #{imageUrl}, '待处理', NOW(), NOW(), 0, #{repairType}, #{urgencyLevel})")
    int insertRepair(RepairApplication repairApplication);



    @Select("SELECT * FROM school_backend_manage.repair_application WHERE user_no = #{userNo} AND is_deleted = 0 ORDER BY create_time DESC")
    List<RepairApplication> getRepairByUser(String userNo);


    List<AnnouncementVO> getAnnouncementList(@Param("offset") int offset, @Param("limit") int limit,Integer priority,String searchKeyword);


    int countAnnouncements(Integer priority,String searchKeyword);

    @Select("SELECT * FROM school_backend_manage.announcement WHERE id = #{id} AND is_deleted = 0")
    Announcement getAnnouncementById(@Param("id") Integer id);


    // 学生提交
    @Insert("INSERT INTO school_backend_manage.complaint_suggestion(user_no, content) VALUES(#{userNo}, #{content})")
    int insertComplaint(ComplaintSuggestion cs);

    // 学生查询（未删除）
    @Select("SELECT * FROM school_backend_manage.complaint_suggestion WHERE user_no = #{userNo} AND is_deleted = 0 ORDER BY create_time DESC")
    List<ComplaintSuggestion> getUserComplaints(@Param("userNo") String userNo);



}
