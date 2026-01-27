package com.dahuang.logistics.mapper;

import com.dahuang.logistics.entity.*;
import com.dahuang.logistics.vo.AnnouncementVO;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface StudentMapper {
    //学生提交换宿申请
    Integer submitApplication(DormChangeApplication dormChangeApplication);
     //是否第一次登录
    @Select("select is_info_completed from school_backend_manage.student_dormitory_info where user_no=#{userNo}")
    Integer firstLoginInfoPage(String userNo);
    // 根据性别查询宿舍楼
    @Select("SELECT id, building_number, assigned_gender, description " +
            "FROM school_backend_manage.build " +
            "WHERE assigned_gender = #{gender}")
    List<Build> getBuildingsByGender(String gender);

    // 根据楼号查询可用宿舍(剩余床位>0且状态正常)
    @Select("SELECT dormitory_id, dormitory_no, building_id, capacity, available_beds, status " +
            "FROM school_backend_manage.dormitory " +
            "WHERE building_id = #{buildingId} AND available_beds > 0 AND status = '正常'")
    List<Dormitory> getAvailableDormsByBuilding(Integer buildingId);

    // 根据宿舍号和楼号查询宿舍
    @Select("SELECT * FROM school_backend_manage.dormitory " +
            "WHERE dormitory_no = #{dormitoryNo} AND building_id = #{buildingId}")
    Dormitory getDormitoryByNo(@Param("dormitoryNo") String dormitoryNo,
                               @Param("buildingId") Integer buildingId);

    // 更新学生宿舍信息
    @Update("UPDATE school_backend_manage.student_dormitory_info " +
            "SET building_id = #{buildingId}, " +
            "    dormitory_number = #{dormitoryNo}, " +
            "    is_info_completed = 1, " +
            "    update_time = CURRENT_TIMESTAMP " +
            "WHERE user_no = #{userNo}")
    int updateStudentDormInfo(@Param("userNo") String userNo,
                              @Param("buildingId") Integer buildingId,
                              @Param("dormitoryNo") String dormitoryNo);

    // 减少宿舍剩余床位
    @Update("UPDATE school_backend_manage.dormitory " +
            "SET available_beds = available_beds - 1 " +
            "WHERE dormitory_no = #{dormitoryNo} AND building_id = #{buildingId} AND available_beds > 0")
    void decreaseDormitoryBeds(@Param("dormitoryNo") String dormitoryNo,
                              @Param("buildingId") Integer buildingId);
    //查看换宿舍申请
    @Select("SELECT * FROM school_backend_manage.dorm_change_application WHERE student_no = #{studentNo} and dorm_change_application.is_deleted = 0 ORDER BY application_time DESC")
    List<DormChangeApplication> getApplicationsByStudent(String studentNo);


    // 根据ID获取申请信息
    DormChangeApplication getApplicationById(@Param("applicationId") Integer applicationId);

    // 撤销申请（软删除）
    int cancelDormApplication(@Param("applicationId") Integer applicationId);

    //维修
    @Insert("INSERT INTO school_backend_manage.repair_application " +
            "(user_no, dormitory_id, content, image_url, status, create_time, update_time, is_deleted, repair_type, urgency_level) " +
            "VALUES (#{userNo}, #{dormitoryId}, #{content}, #{imageUrl}, '待处理', NOW(), NOW(), 0, #{repairType}, #{urgencyLevel})")
    int insertRepair(RepairApplication repairApplication);


    List<RepairApplication> getRepairByUser(@Param("userNo") String userNo,
                                            @Param("status") String status);

    RepairApplication getRepairById(@Param("id") Integer id);


    void cancelRepair(@Param("id") Integer id);

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

    // 获取宿舍信息(联表查询)
    @SuppressWarnings("MybatisXMapperMethodInspection")
    Map<String, Object> getDormInfoByUserNo(String userNo);

    // 获取室友列表
    @SuppressWarnings("MybatisXMapperMethodInspection")
    List<Map<String, Object>> getRoommatesByDormInfo(
            @Param("buildingId") Integer buildingId,
            @Param("dormitoryNumber") String dormitoryNumber,
            @Param("currentUserNo") String currentUserNo
    );

    // 根据学号查询宿舍ID
    Integer getDormitoryIdByUserNo(String userNo);

    String getCurrentDormInfo(String studentNo);

    String getDormNameById(Integer dormitoryId);
}
