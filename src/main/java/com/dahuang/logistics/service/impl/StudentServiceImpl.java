package com.dahuang.logistics.service.impl;

import com.dahuang.logistics.dto.DormChangeApplicationDTO;
import com.dahuang.logistics.dto.RepairApplicationDTO;
import com.dahuang.logistics.entity.*;
import com.dahuang.logistics.mapper.StudentMapper;
import com.dahuang.logistics.service.StudentService;
import com.dahuang.logistics.utils.AIAnnouncementService;
import com.dahuang.logistics.vo.AnnouncementVO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    private static final Logger logger = LoggerFactory.getLogger(DormAdminServiceImpl.class);
    @Autowired
    private StudentMapper studentMapper;

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


}
