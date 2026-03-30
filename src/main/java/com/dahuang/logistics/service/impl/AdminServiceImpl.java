package com.dahuang.logistics.service.impl;

import com.dahuang.logistics.entity.Build;
import com.dahuang.logistics.entity.ComplaintSuggestion;
import com.dahuang.logistics.entity.Dormitory;
import com.dahuang.logistics.entity.User;
import com.dahuang.logistics.mapper.AdminMapper;
import com.dahuang.logistics.service.AdminService;
import com.dahuang.logistics.utils.PasswordUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AdminServiceImpl implements AdminService {


    @Autowired
    private AdminMapper adminMapper;
    public int createUser(User user) {
        // 密码加密
        user.setPassword(PasswordUtils.encode(user.getPassword()));
        return adminMapper.insertUser(user);
    }

    public int resetPassword(String userNo) {
        // 重置为 123456
        String newPassword = PasswordUtils.encode("123456");
        return adminMapper.resetPassword(userNo, newPassword);
    }

    public int updateUser(User user) {
        return adminMapper.updateUser(user);
    }

    public int deleteUser(Integer id) {
        return adminMapper.deleteUser(id);
    }

    public User getUserById(Integer id) {
        return adminMapper.selectUserById(id);
    }

    public List<User> getAllUsers() {
        return adminMapper.selectAllUsers();
    }
    @Override
    public List<ComplaintSuggestion> getAllComplaints() {
       return adminMapper.getAllComplaints();
    }

    @Override
    public int handleComplaint(ComplaintSuggestion cs) {
        cs.setStatus("已处理");
        return adminMapper.handleComplaint(cs);
    }

    @Override
    public int deleteComplaint(Integer id) {
        return adminMapper.deleteComplaint(id);
    }

    @Override
    public Map<String, Integer> getRepairStatusStatistics() {
        List<Map<String, Object>> rawData = adminMapper.repairCountByStatus();
        Map<String, Integer> result = new HashMap<>();

        // 初始化三个状态，防止为0的没统计出来
        result.put("待处理", 0);
        result.put("处理中", 0);
        result.put("已完成", 0);

        for (Map<String, Object> row : rawData) {
            String status = (String) row.get("status");
            Integer count = ((Number) row.get("count")).intValue();
            result.put(status, count);
        }

        return result;
    }

    @Override
    public List<Map<String, Object>> getSuggestStatusStatistics() {
        return adminMapper.suggestCountByStatus();
    }

        @Override
        public List<Map<String, Object>> getDormChangeStatusStats() {
            List<Map<String, Object>> rawList = adminMapper.countDormChangeStatus();
            for (Map<String, Object> item : rawList) {
                String status = (String) item.get("status");
                String statusCn = switch (status) {
                    case "PENDING" -> "待审批";
                    case "APPROVED" -> "已同意";
                    case "REJECTED" -> "已拒绝";
                    default -> "未知状态";
                };
                item.put("status", statusCn);
            }

            return rawList;
        }

    @Override
    public int deleteBuild(Integer id) {
        adminMapper.disableBuild(id);

        return adminMapper.cascadeDeleteDormitory(id);
    }

    @Override
    public int updateBeds(Integer dormitoryId, Integer amount) {
        return adminMapper.updateBeds(dormitoryId, amount);
    }

    @Override
    public int insertBuild(Build build) {
        return adminMapper.insertBuild(build);
    }

    @Override
    public int updateBuild(Build build) {
        return adminMapper.updateBuild(build);
    }

    @Override
    public List<Build> selectBuildList(Build build) {
        return adminMapper.selectBuildList(build);
    }
    @Override
    public int insertDormitory(Dormitory d) {
        return adminMapper.insertDormitory(d);
    }

    @Override
    public int updateDormitory(Dormitory dormitory) {
        return adminMapper.updateDormitory(dormitory);
    }

    @Override
    public int deleteDormitory(Integer id) {
        return adminMapper.deleteDormitory(id);
    }

    @Override
    public List<Dormitory> selectDormitoryList(Dormitory d) {
        return adminMapper.selectDormitoryList(d);
    }
    /**
     * 按类型统计公告数量
     */
    public List<Map<String, Object>> getAnnouncementTypeDistribution() {
        return adminMapper.getAnnouncementTypeDistribution();
    }

    /**
     * 各楼宿舍入住率
     */
    public List<Map<String, Object>> getDormitoryOccupancy() {
        return adminMapper.getDormitoryOccupancy();
    }

    /**
     * 报修状态分布
     */
    public List<Map<String, Object>> getRepairStatusDistribution() {
        return adminMapper.getRepairStatusDistribution();
    }

    /**
     * 用户性别比例
     */
    public List<Map<String, Object>> getUserGenderDistribution() {
        return adminMapper.getUserGenderDistribution();
    }
}
