package com.dahuang.logistics.service.impl;

import com.dahuang.logistics.dto.LoginDTO;
import com.dahuang.logistics.dto.RegisterDTO;
import com.dahuang.logistics.dto.UpdateDTO;
import com.dahuang.logistics.entity.User;
import com.dahuang.logistics.mapper.UserMapper;
import com.dahuang.logistics.service.UserService;
import com.dahuang.logistics.utils.PasswordUtils;
import com.dahuang.logistics.utils.RandomNumber;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    private static final String subject="修改密码一次性代码";
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private RandomNumber randomNumber;
    @Autowired
    private JavaMailSender mailSender;

    //注册
    @Override
    public void register(RegisterDTO registerDTO) {
          registerDTO.setRole(1);//默认学生，管理员端添加管理员
//        if(registerDTO.getRole()==1)
//        {
//            userMapper.insertUserNo(registerDTO.getUserNo());
//        }
        //将学生添加到宿舍住宿表
        userMapper.insertUserNo(registerDTO.getUserNo());
        User user = new User();
        // 将RegisterDTO中的属性复制到User对象中
        BeanUtils.copyProperties(registerDTO, user);
        // 对密码进行加密
        String password=registerDTO.getPassword();
        password = PasswordUtils.encode(password);
        user.setPassword(password);
        user.setCreateTime(new Date());
        // 调用UserMapper的insert方法将用户信息插入数据库
        userMapper.insert(user);
    }
    //登录
    @Override
    public boolean login(LoginDTO loginDTO) {
        // 参数校验
        if (loginDTO == null || loginDTO.getUserNo() == null || loginDTO.getPassword() == null) {
            return false;
        }
        // 获取数据库中的加密密码
        String dbPassword = userMapper.selectPasswordByUserNo(loginDTO);
        // 处理用户不存在的情况
        if (dbPassword == null) {
            return false;
        }
        // 验证密码
        return PasswordUtils.matches(loginDTO.getPassword(), dbPassword);
    }

    @Override
    public boolean updateUser(UpdateDTO updateDTO) {
        int rows = userMapper.updateUser(updateDTO);
        if (rows > 0) {
          return true;
        }
          return false;
    }

    @Override
    public boolean selectUserByUserNo(String userNo) {
        //是否为空
       if (userNo == null) {
           return false;
       }
       //是否查询到数据
        return userMapper.selectUserByUserNo(userNo) != null && userMapper.selectUserByUserNo(userNo) != 0;
    }

    @Override
    public String uploadAvatar(String userNo, MultipartFile file) throws IOException {
        // 获取文件扩展名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));

        // 生成随机文件名
        String newFileName = UUID.randomUUID().toString() + fileExtension;

        // 构造保存路径（项目根目录下的 /imgs 文件夹）
        String rootPath = System.getProperty("user.dir");
        File imgsDir = new File(rootPath, "/src/main/resources/static/imgs");
        if (!imgsDir.exists()) {
            imgsDir.mkdirs();
        }

        File destFile = new File(imgsDir, newFileName);
        file.transferTo(destFile);

        // 构造头像 URL（假设静态资源映射到 /imgs/**）
        String avatarUrl = "http://localhost:8080/imgs/" + newFileName;

        // 更新数据库中该用户的头像地址
        userMapper.updateAvatar(userNo, avatarUrl);

        return avatarUrl;
    }

    @Override
    public  User getUserInfoByUserNo(String userNo) {
        return userMapper.getUserByUserNo(userNo);
    }

    @Override
    public boolean getStatus(String userNo) {
        return userMapper.getStatus(userNo) == null || userMapper.getStatus(userNo) == 0;
    }

    @Override
    public String sendSimpleEmail(String userNo) {
        String to = userMapper.getEmail(userNo); // 查找邮箱
        String randomCode = randomNumber.generateRandomCode();  // 生成六位随机数
        String text = "您的验证码是：" + randomCode;  // 将验证码作为邮件内容
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dahuang1123@qq.com"); // 发件人
        message.setTo(to);                       // 收件人
        message.setSubject(subject);             // 邮件主题
        message.setText(text);                   // 邮件正文
        mailSender.send(message);
        return randomCode; // 返回生成的验证码
    }

    @Override
    public String getEmail(String userNo) {
       return  userMapper.getEmail(userNo);
    }

    @Override
    public String getAvatarUrl(String username) {
        return  userMapper.getAvatarUrl(username);
    }
}