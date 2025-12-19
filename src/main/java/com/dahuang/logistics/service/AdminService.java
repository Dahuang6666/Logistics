package com.dahuang.logistics.service;

import com.dahuang.logistics.entity.ComplaintSuggestion;
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
}
