package com.dahuang.logistics.service.impl;

import com.dahuang.logistics.dto.DormChangeApplicationDTO;
import com.dahuang.logistics.dto.RepairApplicationDTO;
import com.dahuang.logistics.entity.*;
import com.dahuang.logistics.mapper.StudentMapper;
import com.dahuang.logistics.mapper.UserMapper;
import com.dahuang.logistics.service.StudentService;
import com.dahuang.logistics.utils.AIAnnouncementService;
import com.dahuang.logistics.vo.AnnouncementVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LoggerFactory.getLogger(DormAdminServiceImpl.class);
    @Autowired
    private StudentMapper studentMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private AIAnnouncementService aiAnnouncementService;

    @Override
    public int submitApplication(DormChangeApplicationDTO dormChangeApplicationDTO) {
        DormChangeApplication dormChangeApplication = new DormChangeApplication();
        BeanUtils.copyProperties(dormChangeApplicationDTO, dormChangeApplication);
        dormChangeApplication.setApplicationTime(new Date());
        dormChangeApplication.setTargetDormitoryId(000);
        int r= studentMapper.submitApplication(dormChangeApplication);
        return r;
    }

    @Override
    public boolean firstLoginInfoPage(String userNo) {
        Integer result = studentMapper.firstLoginInfoPage(userNo);
        return result != null && result == 1;
    }
    @Override
    public List<DormChangeApplication> getApplicationsByStudent(String studentNo) {
        try {
            return studentMapper.getApplicationsByStudent(studentNo);
        } catch (Exception e) {
            logger.error("获取学生申请记录失败", e);
            return new ArrayList<>();
        }
    }
    @Override
    public boolean createRepair(RepairApplicationDTO dto) {
        try {
            RepairApplication repairApplication = new RepairApplication();
            BeanUtils.copyProperties(dto, repairApplication);
            repairApplication.setStatus("待处理");  // 默认状态
            repairApplication.setIsDeleted(0);     // 正常记录

            RepairAnalysisResult analysis = aiAnnouncementService.analyze(repairApplication.getContent());
            repairApplication.setRepairType(analysis.getType());
            repairApplication.setUrgencyLevel(analysis.getUrgency());

            int result = studentMapper.insertRepair(repairApplication);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<RepairApplication> getRepairsByUser(String userNo) {
        return studentMapper.getRepairByUser(userNo);
    }

    @Override
    public List<AnnouncementVO> getAnnouncementList(int pageNum, int pageSize,Integer priority,String searchKeyword) {
        int offset = (pageNum - 1) * pageSize;
        return studentMapper.getAnnouncementList(offset, pageSize,priority,searchKeyword);
    }

    @Override
    public int getAnnouncementCount(Integer priority,String searchKeyword) {
        return studentMapper.countAnnouncements(priority,searchKeyword);
    }

    @Override
    public Announcement getAnnouncementById(Integer id) {
        return studentMapper.getAnnouncementById(id);
    }

    @Override
    public int insertComplaint(ComplaintSuggestion cs) {
        cs.setId(null);
        cs.setStatus("未处理");
        cs.setReply(null);
        return studentMapper.insertComplaint(cs);
    }

    @Override
    public List<ComplaintSuggestion> getUserComplaints(String userNo) {
        return studentMapper.getUserComplaints(userNo);
    }

    // 根据用户性别获取可选宿舍楼
    public Result getAvailableBuildingsByGender(String userNo) {
        // 1. 获取用户性别
        User user = userMapper.getUserByUserNo(userNo);
        if (user == null) {
            return Result.error("用户不存在");
        }

        String gender = user.getGender();

        // 2. 查询对应性别的宿舍楼
        List<Build> buildings = studentMapper.getBuildingsByGender(gender);

        return Result.success(buildings);
    }

    // 根据楼号获取可用宿舍(剩余床位>0)
    public Result getAvailableDormsByBuilding(Integer buildingId) {
        List<Dormitory> dorms = studentMapper.getAvailableDormsByBuilding(buildingId);
        return Result.success(dorms);
    }

    // 提交首次宿舍信息
    public Result submitFirstDormInfo(String userNo, Integer buildingId, String dormitoryNo) {
        try {
            // 1. 检查宿舍是否还有床位
            Dormitory dorm = studentMapper.getDormitoryByNo(dormitoryNo, buildingId);
            if (dorm == null || dorm.getAvailableBeds() <= 0) {
                return Result.error("该宿舍已满或不存在");
            }
            // 2. 插入或更新学生宿舍信息
            int result = studentMapper.updateStudentDormInfo(userNo, buildingId, dormitoryNo);
            // 3. 更新宿舍剩余床位(-1)
            if (result > 0) {
                studentMapper.decreaseDormitoryBeds(dormitoryNo, buildingId);
                return Result.success("宿舍信息提交成功");
            }
            return Result.error("提交失败");
        } catch (Exception e) {
            return Result.error("系统异常");
        }
    }


    public Result getStudentProfile(String userNo) {
        Map<String, Object> profile = new HashMap<>();

        // 1. 获取用户基本信息
        User user = userMapper.getUserByUserNo(userNo);
        if (user == null) {
            return Result.error("用户不存在");
        }
        profile.put("userInfo", user);

        // 2. 获取宿舍信息(联表查询)
        Map<String, Object> dormInfo = studentMapper.getDormInfoByUserNo(userNo);
        profile.put("dormInfo", dormInfo);

        // 3. 获取室友列表
        if (dormInfo != null) {
            List<Map<String, Object>> roommates = studentMapper.getRoommatesByDormInfo(
                    (Integer) dormInfo.get("buildingId"),
                    (String) dormInfo.get("dormitoryNumber"),
                    userNo
            );
            profile.put("roommates", roommates);
        } else {
            profile.put("roommates", new ArrayList<>());
        }

        return Result.success(profile);
    }

    @Override
    public String uploadAvatar(MultipartFile file) {
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 生成随机文件名
        String newFileName ="repair_"+ UUID.randomUUID().toString() + fileExtension;

        // 构造保存路径（项目根目录下的 /imgs 文件夹）
        String rootPath = System.getProperty("user.dir");
        File imgsDir = new File(rootPath, "/src/main/resources/static/imgs");
        if (!imgsDir.exists()) {
            imgsDir.mkdirs();
        }

        File destFile = new File(imgsDir, newFileName);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // 构造图片 URL（假设静态资源映射到 /imgs/**）
        return  "http://localhost:8080/imgs/" + newFileName;
    }

    @Override
    // 根据学号获取宿舍ID
    public Result getDormitoryIdByUserNo(String userNo) {
        Integer dormitoryId = studentMapper.getDormitoryIdByUserNo(userNo);

        if (dormitoryId != null) {
            return Result.success(dormitoryId);
        } else {
            return Result.error("未找到宿舍信息");
        }
    }
}
