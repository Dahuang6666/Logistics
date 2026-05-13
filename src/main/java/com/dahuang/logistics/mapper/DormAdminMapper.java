package com.dahuang.logistics.mapper;

import com.dahuang.logistics.entity.*;
import com.dahuang.logistics.enums.ApplicationStatus;
import com.dahuang.logistics.vo.DormChangeApplicationVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface DormAdminMapper {

    @Select("select * from school_backend_manage.dorm_change_application where status= #{status}")
    List<DormChangeApplicationVO> getAllApplication(ApplicationStatus status);

    @Update("update  school_backend_manage.dorm_change_application " +
            "set status = #{status}, approval_time = #{approvalTime}, " +
            "approver_no = #{approverNo}, comment = #{comment} " +
            "where application_id = #{applicationId}")
    int updateApplication(DormChangeApplication application);
    // 查所有
    @Select("SELECT * FROM school_backend_manage.dorm_change_application ORDER BY application_time DESC")
    List<DormChangeApplicationVO> getAllApplicationList();

    // 按状态查
    @Select("SELECT * FROM school_backend_manage.dorm_change_application WHERE status = #{status} ORDER BY application_time DESC")
    List<DormChangeApplicationVO> getApplicationsByStatus(String status);

    @Select("SELECT * FROM school_backend_manage.repair_application WHERE is_deleted = 0 ORDER BY create_time DESC")
    List<RepairApplication> getAllRepairs();

    @Update("UPDATE school_backend_manage.repair_application SET status = #{status}, update_time = NOW() WHERE id = #{id}")
    int updateStatus(@Param("id") Integer id, @Param("status") String status);

    @Update("UPDATE school_backend_manage.repair_application SET is_deleted = 1, update_time = NOW() WHERE id = #{id}")
    int softDeleteRepair(@Param("id") Integer id);

    // 查询所有公告（包括发布单位、内容、时间）
    @Select("SELECT * FROM school_backend_manage.announcement WHERE is_deleted = 0 ORDER BY publish_time DESC")
    List<Announcement> getAllAnnouncements();

    // 修改公告
    @Update("UPDATE school_backend_manage.announcement " +
            "SET title = #{title}, content = #{content}, " +
            "publisher = #{publisher},announcement_type_id=#{announcementTypeId}, " +
            "publish_time = NOW() " +
            "WHERE id = #{id} AND is_deleted = 0")
    int updateAnnouncement(Announcement announcement);

    // 逻辑删除公告
    @Update("UPDATE school_backend_manage.announcement SET is_deleted = 1 WHERE id = #{id}")
    int deleteAnnouncement(@Param("id") Integer id);


    // 新增公告
    @Insert("INSERT INTO school_backend_manage.announcement(title, content, publisher, announcement_type_id) " +
            "VALUES(#{title}, #{content}, #{publisher}, #{announcementTypeId})")
    int insertAnnouncement(Announcement announcement);

    @Select("SELECT id, type_name as typeName, priority, created_date as createdDate " +
            "FROM school_backend_manage.announcement_type " +
            "ORDER BY priority DESC")
    List<AnnouncementType> selectAllTypes();


    // 1. 新增公告类型
    @Insert("INSERT INTO announcement_type(type_name, priority) VALUES(#{typeName}, #{priority})")
    int insertAnnouncementType(AnnouncementType type);

    // 2. 删除类型前的迁移：将该类型下的公告全部改为 ID=1 (未知类型)
    @Update("UPDATE announcement SET announcement_type_id = 1 WHERE announcement_type_id = #{id}")
    void migrateAnnouncementsToUnknown(@Param("id") Integer id);

    // 3. 物理删除类型
    @Delete("DELETE FROM announcement_type WHERE id = #{id}")
    int deleteAnnouncementTypeById(@Param("id") Integer id);

    @Update("UPDATE announcement_type SET type_name = #{typeName}, priority = #{priority} WHERE id = #{id}")
    int updateAnnouncementType(AnnouncementType type);

    @Select("SELECT dormitory_no FROM school_backend_manage.dormitory WHERE dormitory_id = #{dormitoryId} AND is_delete = 0")
    String getDormitoryNoById(Integer dormitoryId);

    List<Dormitory> getAvailableDorms(Integer buildingId);

    @Select("SELECT gender FROM school_backend_manage.user WHERE user_no = #{studentNo} AND status = 1")
    String getStudentGender(@Param("studentNo") String studentNo);

    @Select("SELECT b.id as id, b.building_number as buildingNumber, b.assigned_gender as assignedGender " +
            "FROM school_backend_manage.build b " +
            "WHERE  b.status = '正常' AND b.assigned_gender = #{gender}")
    List<Build> getAvailableBuildingsByGender(@Param("gender") String gender);

    @Update("UPDATE school_backend_manage.dorm_change_application SET target_dormitory_id = #{targetDormId} WHERE application_id = #{applicationId}")
    int updateTargetDorm(@Param("applicationId") Integer applicationId, @Param("targetDormId") Integer targetDormId);

    // 获取申请详情（包括学号、当前宿舍ID、目标宿舍ID、审批意见等）
    @Select("SELECT application_id as applicationId, student_no as studentNo, " +
            "current_dormitory_id as currentDormitoryId, target_dormitory_id as targetDormitoryId, " +
            "reason, status, application_time as applicationTime, comment, " +
            "approver_no as approverNo, approval_time as approvalTime " +
            "FROM school_backend_manage.dorm_change_application WHERE application_id = #{applicationId}")
    DormChangeApplicationVO getApplicationById(@Param("applicationId") Integer applicationId);

    // 获取宿舍所属楼栋ID
    @Select("SELECT building_id FROM school_backend_manage.dormitory WHERE dormitory_id = #{dormitoryId}")
    Integer getBuildingIdByDormitoryId(@Param("dormitoryId") Integer dormitoryId);

    // 获取宿舍号
    @Select("SELECT dormitory_no FROM school_backend_manage.dormitory WHERE dormitory_id = #{dormitoryId}")
    String getDormitoryNo(@Param("dormitoryId") Integer dormitoryId);

    // 更新学生住宿信息表
    @Update("UPDATE school_backend_manage.student_dormitory_info " +
            "SET building_id = #{buildingId}, dormitory_number = #{dormitoryNumber}, is_info_completed = 1 " +
            "WHERE user_no = #{userNo}")
    int updateStudentDormitoryInfo(@Param("userNo") String userNo, 
                                   @Param("buildingId") Integer buildingId, 
                                   @Param("dormitoryNumber") String dormitoryNumber);

    // 插入学生住宿信息（如果不存在）
    @Insert("INSERT INTO school_backend_manage.student_dormitory_info(user_no, building_id, dormitory_number, is_info_completed) " +
            "VALUES(#{userNo}, #{buildingId}, #{dormitoryNumber}, 1)")
    int insertStudentDormitoryInfo(@Param("userNo") String userNo, 
                                   @Param("buildingId") Integer buildingId, 
                                   @Param("dormitoryNumber") String dormitoryNumber);

    // 检查学生住宿信息是否存在
    @Select("SELECT COUNT(*) FROM school_backend_manage.student_dormitory_info WHERE user_no = #{userNo}")
    int checkStudentDormitoryExists(@Param("userNo") String userNo);

    // 原宿舍床位+1
    @Update("UPDATE school_backend_manage.dormitory SET available_beds = available_beds + 1 WHERE dormitory_id = #{dormitoryId}")
    int increaseAvailableBeds(@Param("dormitoryId") Integer dormitoryId);

    // 目标宿舍床位-1
    @Update("UPDATE school_backend_manage.dormitory SET available_beds = available_beds - 1 WHERE dormitory_id = #{dormitoryId}")
    int decreaseAvailableBeds(@Param("dormitoryId") Integer dormitoryId);
}
