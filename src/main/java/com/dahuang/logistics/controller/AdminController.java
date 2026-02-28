package com.dahuang.logistics.controller;


import com.dahuang.logistics.entity.*;
import com.dahuang.logistics.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/school/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @PostMapping("/createUser")
    public Result  createUser(@RequestBody User user) {
        adminService.createUser(user);
        return Result.success();
    }
    //重置密码
    @PostMapping("/resetPassword")
    public Result resetPassword(@RequestBody String userNo) {
        adminService.resetPassword(userNo);
        return Result.success();
    }

    @PutMapping("/updateUser")
    public Result updateUser(@RequestBody User user) {
        adminService.updateUser(user);
        return Result.success();
    }

    @DeleteMapping("/deleteUser/{id}")
    public Result deleteUser(@PathVariable Integer id) {
        adminService.deleteUser(id);
        return Result.success();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable Integer id) {
        return adminService.getUserById(id);
    }

    @GetMapping("/getListUser")
    public Result getListUser() {
        return Result.success(adminService.getAllUsers());
    }
    //获取所有建议
    @GetMapping("/getListSuggest")
    public Result getListSuggest() {
        return Result.success(adminService.getAllComplaints());
    }
    //处理建议
    @PutMapping("/handle")
    public Result handle(@RequestBody ComplaintSuggestion cs) {
        return adminService.handleComplaint(cs) > 0 ?
                Result.success() : Result.error("处理失败");
    }
   //删除建议
    @DeleteMapping("/deleteSuggest/{id}")
    public Result deleteSuggest(@RequestParam Integer id) {
        return adminService.deleteComplaint(id) > 0 ?
                Result.success() : Result.error("删除失败");
    }
    //报修数量统计
    @GetMapping("/repairCount")
    public Result getRepairStatusStats() {
        Map<String, Integer> stats = adminService.getRepairStatusStatistics();
        return Result.success(stats);
    }

    //建议数量
    @GetMapping("/suggestCount")
    public Result suggestCountByStatus() {
        List<Map<String, Object>> stats = adminService.getSuggestStatusStatistics();
        return Result.success(stats);
    }

   //宿舍申请数量
    @GetMapping("/dormCountByStatus")
    public Result countByStatus() {
        List<Map<String, Object>> stats = adminService.getDormChangeStatusStats();
        return Result.success(stats);
    }

    // --- 宿舍楼管理 ---
    @PostMapping("/build")
    public Result addBuild(@RequestBody Build build) {
        return adminService.insertBuild(build) > 0 ? Result.success() : Result.error("添加失败");
    }

    @GetMapping("/build/list")
    public Result listBuild(Build build) { // 接收组合查询参数
        return Result.success(adminService.selectBuildList(build));
    }

    @DeleteMapping("/build/{id}")
    public Result deleteBuild(@PathVariable Integer id) {
        adminService.deleteBuild(id);
        return Result.success();
    }

    // --- 宿舍管理 ---
    @GetMapping("/dorm/list")
    public Result listDorm(Dormitory dormitory) {
        return Result.success(adminService.selectDormitoryList(dormitory));
    }

    @PutMapping("/dorm/updateBeds")
    public Result updateBeds(Integer id, Integer amount) {
        int res = adminService.updateBeds(id, amount);
        return res > 0 ? Result.success() : Result.error("修改失败，请检查床位上限或余量");
    }
}
