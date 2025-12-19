package com.dahuang.logistics.mapper;


import com.dahuang.logistics.entity.ComplaintSuggestion;
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


}
