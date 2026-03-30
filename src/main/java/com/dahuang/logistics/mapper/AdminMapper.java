package com.dahuang.logistics.mapper;


import com.dahuang.logistics.entity.Build;
import com.dahuang.logistics.entity.ComplaintSuggestion;
import com.dahuang.logistics.entity.Dormitory;
import com.dahuang.logistics.entity.User;
import org.apache.ibatis.annotations.*;

import java.util.List;
import java.util.Map;

@Mapper
public interface AdminMapper {
    @Insert("INSERT INTO school_backend_manage.user " +
            "(user_no, password, username, role, phone, email, status, gender) " +
            "VALUES (#{userNo}, #{password}, #{username}, #{role}, #{phone}, #{email}, #{status}, #{gender})")
    int insertUser(User user);

    @Update("UPDATE school_backend_manage.user " +
            "SET password = #{password} " +
            "WHERE user_no = #{userNo}")
    int resetPassword(@Param("userNo") String userNo,
                      @Param("password") String password);

    @Update("UPDATE school_backend_manage.user " +
            "SET username=#{username}, role=#{role}, phone=#{phone}, email=#{email}, status=#{status}, gender=#{gender} " +
            "WHERE id=#{id}")
    int updateUser(User user);

    @Delete("DELETE FROM school_backend_manage.user WHERE id=#{id}")
    int deleteUser(Integer id);

    @Select("SELECT id, user_no, '已加密' as password, username, role, phone, email, status, gender " +
            "FROM school_backend_manage.user WHERE id=#{id}")
    User selectUserById(Integer id);

    @Select("SELECT id, user_no, '已加密' as password, username, role, phone, email, status, gender " +
            "FROM school_backend_manage.user")
    List<User> selectAllUsers();

    // 管理端查看所有申请
    @Select("SELECT * FROM school_backend_manage.complaint_suggestion WHERE is_deleted = 0 ORDER BY create_time DESC")
    List<ComplaintSuggestion> getAllComplaints();

    // 管理端处理（更新状态+回复）
    @Update("UPDATE school_backend_manage.complaint_suggestion SET status = #{status}, reply = #{reply} WHERE id = #{id}")
    int handleComplaint(ComplaintSuggestion cs);

    // 逻辑删除
    @Update("UPDATE school_backend_manage.complaint_suggestion SET is_deleted = 1 WHERE id = #{id}")
    int deleteComplaint(@Param("id") Integer id);

       //数据统计
        @Select("SELECT status, COUNT(*) AS count " +
                "FROM school_backend_manage.repair_application " +
                "WHERE is_deleted = 0 " +
                "GROUP BY status")
        List<Map<String, Object>> repairCountByStatus();


        @Select("SELECT status, COUNT(*) AS count " +
                "FROM school_backend_manage.complaint_suggestion " +
                "WHERE is_deleted = 0 " +
                "GROUP BY status")
        List<Map<String, Object>> suggestCountByStatus();


    @Select("SELECT status, COUNT(*) AS count " +
            "FROM school_backend_manage.dorm_change_application " +
            "WHERE is_deleted = 0 " +
            "GROUP BY status")
    List<Map<String, Object>> countDormChangeStatus();


    // 宿舍楼
    @Insert("INSERT INTO build(building_number, assigned_gender, description, status) " +
            "VALUES(#{buildingNumber}, #{assignedGender}, #{description}, #{status})")
    int insertBuild(Build build);

    @Update("UPDATE build SET building_number=#{buildingNumber}, assigned_gender=#{assignedGender}, " +
            "description=#{description}, status=#{status} WHERE id=#{id}")
    int updateBuild(Build build);

    @Update("UPDATE build SET status='异常' WHERE id=#{id}")
    int disableBuild(Integer id);

    List<Build> selectBuildList(Build build);

    // 宿舍
    @Insert("INSERT INTO dormitory(dormitory_no, building_id, capacity, available_beds, status) " +
            "VALUES(#{dormitoryNo}, #{buildingId}, #{capacity}, #{availableBeds}, #{status})")
    int insertDormitory(Dormitory dormitory);

    // 逻辑删除宿舍
    @Update("UPDATE dormitory SET is_delete = 1 WHERE dormitory_id = #{id}")
    int deleteDormitory(Integer id);

    @Update("UPDATE dormitory SET dormitory_no=#{dormitoryNo}, building_id=#{buildingId}, " +
            "capacity=#{capacity}, available_beds=#{availableBeds}, status=#{status} WHERE dormitory_id=#{dormitoryId}")
    int updateDormitory(Dormitory dormitory);


    @Update("UPDATE dormitory SET available_beds = available_beds + #{amount} " +
            "WHERE dormitory_id = #{id} AND (available_beds + #{amount}) >= 0 AND (available_beds + #{amount}) <= capacity")
    int updateBeds(@Param("id") Integer id, @Param("amount") Integer amount);

    List<Dormitory> selectDormitoryList(Dormitory dormitory);

    int cascadeDeleteDormitory(Integer buildingId);

    /**
     * 按类型统计公告数量
     */
    @Select("SELECT " +
            "    at.type_name AS name, " +
            "    COUNT(a.id) AS value " +
            "FROM announcement_type at " +
            "LEFT JOIN announcement a ON at.id = a.announcement_type_id AND a.is_deleted = 0 " +
            "GROUP BY at.id, at.type_name " +
            "ORDER BY value DESC")
    List<Map<String, Object>> getAnnouncementTypeDistribution();

    /**
     * 各楼宿舍入住率
     */
    @Select("SELECT " +
            "    b.building_number AS buildingNumber, " +
            "    SUM(d.capacity) AS totalCapacity, " +
            "    SUM(d.capacity - d.available_beds) AS occupiedBeds, " +
            "    ROUND(SUM(d.capacity - d.available_beds) * 100.0 / SUM(d.capacity), 2) AS occupancyRate " +
            "FROM build b " +
            "LEFT JOIN dormitory d ON b.id = d.building_id AND d.is_delete = 0 " +
            "WHERE b.status = '正常' " +
            "GROUP BY b.id, b.building_number " +
            "ORDER BY b.building_number")
    List<Map<String, Object>> getDormitoryOccupancy();

    /**
     * 报修状态分布
     */
    @Select("SELECT " +
            "    status AS name, " +
            "    COUNT(*) AS value " +
            "FROM repair_application " +
            "WHERE is_deleted = 0 " +
            "GROUP BY status " +
            "ORDER BY FIELD(status, '待处理', '处理中', '已完成')")
    List<Map<String, Object>> getRepairStatusDistribution();

    /**
     * 用户性别比例
     */
    @Select("SELECT " +
            "    gender AS name, " +
            "    COUNT(*) AS value " +
            "FROM user " +
            "WHERE status = 1 AND gender IS NOT NULL " +
            "GROUP BY gender")
    List<Map<String, Object>> getUserGenderDistribution();

}
