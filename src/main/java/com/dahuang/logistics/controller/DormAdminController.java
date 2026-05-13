package com.dahuang.logistics.controller;

import com.dahuang.logistics.dto.ApplicationStatusRequest;
import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.AnnouncementType;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.Result;
import com.dahuang.logistics.service.DormAdminService;
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

    // 发布新公告
    @PostMapping("/addAnnouncement")
    public Result addAnnouncement(@RequestBody Announcement announcement) {
        if (dormAdminService.addAnnouncement(announcement)) {
            return Result.success();
        } else {
            return Result.error("公告发布失败");
        }
    }
    // 修改公告
    @PutMapping("/updateAnnouncement")
    public Result updateAnnouncement(@RequestBody Announcement announcement) {
        if (dormAdminService.updateAnnouncement(announcement)) {
            return Result.success();
        } else {
            return Result.error("公告修改失败");
        }
    }

    // 删除公告
    @DeleteMapping("/deleteAnnouncement")
    public Result deleteAnnouncement(@RequestParam Integer id) {
        if (dormAdminService.deleteAnnouncement(id)) {
            return Result.success();
        } else {
            return Result.error("删除失败");
        }
    }

    @GetMapping("/getAllTypes")
    public Result getAllTypes() {
        List<AnnouncementType> types = dormAdminService.findAllAnnouncementTypes();
        if (types != null) {
            return Result.success(types);
        } else {
            return Result.error("获取类型列表失败");
        }
    }

    // 新增公告类型
    @PostMapping("/addType")
    public Result addType(@RequestBody AnnouncementType type) {
        if (dormAdminService.addAnnouncementType(type)) {
            return Result.success();
        } else {
            return Result.error("新增分类失败");
        }
    }

    // 删除公告类型
    @DeleteMapping("/deleteType")
    public Result deleteType(@RequestParam Integer id) {
        // 尝试删除
        if (dormAdminService.deleteAnnouncementType(id)) {
            return Result.success();
        } else {
            return Result.error("删除失败：该类型为系统内置或操作异常");
        }
    }

    /**
     * 修改公告类型
     */
    @PutMapping("/updateType")
    public Result updateType(@RequestBody AnnouncementType type) {
        // 基本参数校验
        if (type.getId() == null) {
            return Result.error("缺少类型ID");
        }
        if (dormAdminService.updateAnnouncementType(type)) {
            return Result.success();
        } else {
            return Result.error("修改分类失败");
        }
    }
    // 新增：根据宿舍ID查询宿舍号接口
    @GetMapping("/getDormitoryNoById")
    public Result getDormitoryNoById(@RequestParam Integer dormitoryId) {
        String dormitoryNo = dormAdminService.getDormitoryNoById(dormitoryId);
        if (dormitoryNo == null) {
            return Result.error("未找到该宿舍信息");
        }
        return Result.success(dormitoryNo);
    }

    //获取可用宿舍列表(有剩余床位且状态正常)
    @GetMapping("/getAvailableDorms")
    public Result getAvailableDorms(@RequestParam(required = false) Integer buildingId) {
        return Result.success(dormAdminService.getAvailableDorms(buildingId));
    }

    //根据学生性别获取可用宿舍楼
    @GetMapping("/getAvailableBuildingsByGender")
    public Result getAvailableBuildingsByGender(@RequestParam String studentNo) {
        return dormAdminService.getAvailableBuildingsByGender(studentNo);
    }

    // 修改申请的目标宿舍
    @PostMapping("/updateTargetDorm")
    public Result updateTargetDorm(@RequestParam Integer applicationId, @RequestParam Integer targetDormId) {
        boolean success = dormAdminService.updateTargetDorm(applicationId, targetDormId);
        return success ? Result.success("目标宿舍更新成功") : Result.error("目标宿舍更新失败");
    }

    // 根据ID获取申请详情
    @GetMapping("/getApplicationById")
    public Result getApplicationById(@RequestParam Integer applicationId) {
        DormChangeApplicationVO vo = dormAdminService.getApplicationById(applicationId);
        if (vo == null) {
            return Result.error("未找到该申请");
        }
        return Result.success(vo);
    }

}
