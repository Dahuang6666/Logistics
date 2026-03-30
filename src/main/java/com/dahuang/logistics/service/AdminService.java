package com.dahuang.logistics.service;

import com.dahuang.logistics.entity.Build;
import com.dahuang.logistics.entity.ComplaintSuggestion;
import com.dahuang.logistics.entity.Dormitory;
import com.dahuang.logistics.entity.User;

import java.util.List;
import java.util.Map;

public interface AdminService {
    Object getAllComplaints();

    int handleComplaint(ComplaintSuggestion cs);

    int deleteComplaint(Integer id);

    Map<String, Integer> getRepairStatusStatistics();

    List<Map<String,Object>> getSuggestStatusStatistics();

    List<Map<String,Object>>  getDormChangeStatusStats();

    int createUser(User user);

    int updateUser(User user);

   int deleteUser(Integer id);

    User getUserById(Integer id);

    List<User> getAllUsers();

    int resetPassword(String userNo);

    // 宿舍楼管理
    int insertBuild(Build build);

    int updateBuild(Build build);

    int deleteBuild(Integer id); // 设为异常并级联删除宿舍

    List<Build> selectBuildList(Build build); // 条件查询

    // 宿舍管理
    int insertDormitory(Dormitory dormitory);

    int updateDormitory(Dormitory dormitory);

    int deleteDormitory(Integer id);

    List<Dormitory> selectDormitoryList(Dormitory dormitory); // 条件查询

    int updateBeds(Integer dormitoryId, Integer amount); // 手动增减床位

    List<Map<String, Object>> getAnnouncementTypeDistribution();

    List<Map<String, Object>> getDormitoryOccupancy();

    List<Map<String, Object>> getRepairStatusDistribution();

    List<Map<String, Object>> getUserGenderDistribution();
}
