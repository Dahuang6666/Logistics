package com.dahuang.logistics.controller;

import com.dahuang.logistics.dto.ApplicationStatusRequest;
import com.dahuang.logistics.entity.AIAnnouncement;
import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.Result;
import com.dahuang.logistics.service.DormAdminService;
import com.dahuang.logistics.utils.AIAnnouncementService;
import com.dahuang.logistics.vo.DormChangeApplicationVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/school/dormAdmin")
public class DormAdminController {
    @Autowired
    private DormAdminService dormAdminService;
     @Autowired
     private AIAnnouncementService aiAnnouncementService;
    //获取待审批所有变更宿舍申请
    @GetMapping("/getAllApplication")
     public Result getPENDINGApplication(){
        List<DormChangeApplicationVO> dormChangeApplicationVO
                = dormAdminService.getAllApplication();
        return Result.success(dormChangeApplicationVO);
    }
    //更新变更宿舍申请状态
    @PostMapping("/updateApplicationStatus")
    public Result updateApplicationStatus(@RequestBody ApplicationStatusRequest request) {
        DormChangeApplication application = new DormChangeApplication();
        BeanUtils.copyProperties(request,application);
        application.setApprovalTime(new Date());
        boolean success = dormAdminService.updateApplication(application);
        return success ? Result.success("状态更新成功") : Result.error("状态更新失败");
    }
    //根据状态获取变更宿舍信息
    @GetMapping("/getApplicationList")
    public Result getApplicationList(@RequestParam(required = false) String status) {
        List<DormChangeApplicationVO> list = dormAdminService.getApplicationList(status);
        return Result.success(list);
    }

    // 管理员查看全部报修
    @GetMapping("/all")
    public Result getAllRepairs() {
        return Result.success(dormAdminService.getAllRepairs());
    }

    // 管理员更新报修状态
    @PutMapping("/updateStatus")
    public Result updateStatus(@RequestParam Integer id, @RequestParam String status) {
        if (dormAdminService.updateStatus(id, status)) {
            return Result.success();
        } else {
            return Result.error("状态更新失败");
        }
    }
    // 管理员删除报修（软删除）
    @DeleteMapping("/delete")
    public Result deleteRepair(@RequestParam Integer id) {
        if (dormAdminService.deleteRepair(id)) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }
//    // 查看所有公告
//    @GetMapping("/listAnnouncement")
//    public Result getAllAnnouncements() {
//        List<Announcement> list = dormAdminService.getAllAnnouncements();
//        return Result.success(list);
//    }
//     //写公告
//    @PostMapping("/getchat")
//    public String getAIAnnouncement(@RequestBody AIAnnouncement dto) {
//        return aiAnnouncementService.generateAnnouncement(
//            dto.getTopic(),
//            dto.getPublisher(),
//            dto.getPublishDate(),
//            dto.getSummary()
//       );
//    }

//    // 修改公告
//    @PutMapping("/updateAnnouncement")
//    public Result updateAnnouncement(@RequestBody Announcement announcement) {
//        if (dormAdminService.updateAnnouncement(announcement)) {
//            return Result.success();
//        } else {
//            return Result.error("公告修改失败");
//        }
//    }

//    // 删除公告
//    @DeleteMapping("/deleteAnnouncement")
//    public Result deleteAnnouncement(@RequestParam Integer id) {
//        if (dormAdminService.deleteAnnouncement(id)) {
//            return Result.success();
//        } else {
//            return Result.error("删除失败");
//        }
//    }

}
