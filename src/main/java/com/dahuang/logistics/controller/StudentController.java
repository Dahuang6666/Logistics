package com.dahuang.logistics.controller;
import com.dahuang.logistics.dto.DormChangeApplicationDTO;
import com.dahuang.logistics.dto.RepairApplicationDTO;
import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.ComplaintSuggestion;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.Result;
import com.dahuang.logistics.service.StudentService;
import com.dahuang.logistics.vo.AnnouncementVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/school/student")
public class StudentController {
    @Autowired
    private StudentService studentService;

    // 学生提交换宿申请
    @PostMapping("/application")
    public Result submitApplication(@RequestBody DormChangeApplicationDTO dormChangeApplicationDTO) {
        int result = studentService.submitApplication(dormChangeApplicationDTO);
        return result > 0 ? Result.success("提交申请成功") : Result.error("提交失败");
    }

    // 首次登录信息填写页面
    @GetMapping("/firstLoginInfo")
    public boolean firstLoginInfoPage( @RequestParam String userNo) {
        // 获取当前登录学生信息
       return  studentService.firstLoginInfoPage(userNo);
    }

    //获取变更宿舍申请进度
    @GetMapping("/getMyDormApplications")
    public Result getMyDormApplications(@RequestParam String studentNo) {
        List<DormChangeApplication> list = studentService.getApplicationsByStudent(studentNo);
        return Result.success(list);
    }
    //学生报修申请
    @PostMapping("/submitRepair")
    public Result submitRepair(@RequestBody RepairApplicationDTO dto) {
        if (studentService.createRepair(dto)) {
            return Result.success();
        } else {
            return Result.error("提交失败");
        }
    }
    // 学生查询自己的报修单
    @GetMapping("/myRepairs")
    public Result getMyRepairs(@RequestParam String userNo) {
        return Result.success(studentService.getRepairsByUser(userNo));
    }

    @GetMapping("/announcementList")
    public Result getAnnouncementList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) Integer priority
    ) {
        List<AnnouncementVO> list =
                studentService.getAnnouncementList(pageNum, pageSize, priority);
        int total =
                studentService.getAnnouncementCount(priority);

        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);
        return Result.success(result);
    }

    //公告详细查看
//    @GetMapping("/detail")
//    public Result getAnnouncementDetail(@RequestParam Integer id) {
//        Announcement announcement = studentService.getAnnouncementById(id);
//        if (announcement != null) {
//            return Result.success(announcement);
//        } else {
//            return Result.error("公告不存在或已删除");
//        }
//    }

    @PostMapping("/submitSuggest")
    public Result submit(@RequestBody ComplaintSuggestion cs) {
        return studentService.insertComplaint(cs) > 0 ? Result.success() : Result.error("提交失败");
    }

    @GetMapping("/mySuggestList")
    public Result myComplaints(@RequestParam String userNo) {
        return Result.success(studentService.getUserComplaints(userNo));
    }

}

