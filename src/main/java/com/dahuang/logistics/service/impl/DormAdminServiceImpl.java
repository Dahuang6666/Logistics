package com.dahuang.logistics.service.impl;
import com.dahuang.logistics.entity.Announcement;
import com.dahuang.logistics.entity.DormChangeApplication;
import com.dahuang.logistics.entity.RepairApplication;
import com.dahuang.logistics.enums.ApplicationStatus;
import com.dahuang.logistics.mapper.DormAdminMapper;
import com.dahuang.logistics.service.DormAdminService;
import com.dahuang.logistics.vo.DormChangeApplicationVO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import java.util.ArrayList;
import java.util.List;


@Service
public class DormAdminServiceImpl implements DormAdminService {
    private static final Logger logger = LoggerFactory.getLogger(DormAdminServiceImpl.class);

    @Autowired
    private DormAdminMapper dormAdminMapper;
    @Override
    public List<DormChangeApplicationVO> getAllApplication() {
        try {
            ApplicationStatus status = ApplicationStatus.PENDING;
            logger .error(status.getDesc());
            return dormAdminMapper.getAllApplication(status);
        } catch (Exception e) {
            logger.error("获取换宿申请列表时出错", e);
            return new ArrayList<>();
        }
    }

    @Override
    public boolean updateApplication(DormChangeApplication application) {
        try {
            int rows = dormAdminMapper.updateApplication(application);
            return rows > 0;
        } catch (Exception e) {
            logger.error("更新宿舍调换申请状态失败", e);
            return false;
        }
    }
    @Override
    public List<DormChangeApplicationVO> getApplicationList(String status) {
        try {
            if (status == null || status.equals("全部")) {
                return dormAdminMapper.getAllApplicationList();
            } else {
                return dormAdminMapper.getApplicationsByStatus(status);
            }
        } catch (Exception e) {
            logger.error("获取申请记录失败", e);
            return new ArrayList<>();
        }
    }
    @Override
    public List<RepairApplication> getAllRepairs() {
        return dormAdminMapper.getAllRepairs();
    }

    @Override
    public boolean updateStatus(Integer id, String status) {
        try {
            int result = dormAdminMapper.updateStatus(id, status);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteRepair(Integer id) {
        try {
            int result =dormAdminMapper.softDeleteRepair(id);
            return result > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
    @Override
    public List<Announcement> getAllAnnouncements() {
        return dormAdminMapper.getAllAnnouncements();
    }

    @Override
    public boolean updateAnnouncement(Announcement announcement) {
        try {
            return dormAdminMapper.updateAnnouncement(announcement) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteAnnouncement(Integer id) {
        try {
            return dormAdminMapper.deleteAnnouncement(id) > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
