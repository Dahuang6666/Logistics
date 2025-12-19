package com.dahuang.logistics.mapper;

import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.RepairApplication;
import com.dahuang.logistics.enums.ApplicationStatus;
import com.dahuang.logistics.vo.DormChangeApplicationVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface DormAdminMapper {

    @Select("select * from school_backend_manage.dorm_change_application where status= #{status.desc}")
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
            "SET title = #{title}, content = #{content}, publisher = #{publisher}, publish_time = NOW() " +
            "WHERE id = #{id} AND is_deleted = 0")
    int updateAnnouncement(Announcement announcement);

    // 逻辑删除公告
    @Update("UPDATE school_backend_manage.announcement SET is_deleted = 1 WHERE id = #{id}")
    int deleteAnnouncement(@Param("id") Integer id);


}
