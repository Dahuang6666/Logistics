package com.dahuang.logistics.controller;
import com.dahuang.logistics.dto.LoginDTO;
import com.dahuang.logistics.dto.RegisterDTO;
import com.dahuang.logistics.dto.RepairApplicationDTO;
import com.dahuang.logistics.dto.UpdateDTO;
import com.dahuang.logistics.entity.Result;
import com.dahuang.logistics.entity.User;
import com.dahuang.logistics.service.UserService;
import com.dahuang.logistics.utils.PasswordUtils;
import com.google.code.kaptcha.Producer;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/school/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private Producer captchaProducer;
    private final Map<String, String> captchaStorage = new ConcurrentHashMap<>();
    //注册
    @PostMapping("/register")
    public Result register(@RequestBody RegisterDTO registerDTO) {
        LoginDTO loginDTO = new LoginDTO();
        //查询账号是否存在
        BeanUtils.copyProperties(registerDTO,loginDTO);
        if(userService.selectUserByUserNo(loginDTO.getUserNo())){
            return Result.error("账号已注册");
        }
        userService.register(registerDTO);
        return Result.success();
    }

    //登录
    @PostMapping("/login")
    public Result login(@RequestBody LoginDTO loginDTO) {
        if(!userService.selectUserByUserNo(loginDTO.getUserNo())){
            return Result.error("账号不存在");
        }
        if(userService.getStatus(loginDTO.getUserNo()))
        {
            return Result.error("帐户被禁用");
        }
        if (userService.login(loginDTO)) {
            return Result.success();
        }
        return Result.error("账号或密码错误");
    }


    //修改
    @PutMapping("/updateUserInfo")
    public Result updateUserInfo( @RequestBody UpdateDTO updateDTO) {

        LoginDTO loginDTO = new LoginDTO();
        BeanUtils.copyProperties(updateDTO,loginDTO);
        //查询是否存在账号
        if(!userService.selectUserByUserNo(loginDTO.getUserNo())){
            return Result.error("账号不存在");
        }

        // 对密码进行加密处理
        if (updateDTO.getPassword() != null && !updateDTO.getPassword().isEmpty()) {
            String encodedPassword = PasswordUtils.encode(updateDTO.getPassword());
            updateDTO.setPassword(encodedPassword);
        }

        // 调用服务层方法更新用户信息
        boolean isUpdated = userService.updateUser(updateDTO);
        if (isUpdated) {
            return Result.success();
        } else {
            return Result.error("更新用户信息失败");
        }
    }
    //上传头像
    @PostMapping("/uploadAvatar")
    public Result uploadAvatar(@RequestParam("userNo") String userNo,
                               @RequestParam("file") MultipartFile file) {
        try {
            String avatarUrl = userService.uploadAvatar(userNo, file);
            return Result.success(avatarUrl); // 返回头像地址
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("头像上传失败");
        }
    }
    @GetMapping("/getUserInfo")
    public Result getUserInfo(@RequestParam("userNo") String userNo) {
        User user = userService.getUserInfoByUserNo(userNo);
        if (user != null) {
            return Result.success(user);
        } else {
            return Result.error("用户不存在");
        }
    }
    // 生成验证码
    @GetMapping("/captcha")
    public Map<String, String> getCaptcha() throws Exception {
        // 生成验证码文本
        String text = captchaProducer.createText();
        System.out.println("验证码：" + text);
        // 生成验证码图片
        BufferedImage image = captchaProducer.createImage(text);
        // 将图片转换为 Base64 编码
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        String base64Image = Base64.getEncoder().encodeToString(baos.toByteArray());
        // 生成唯一标识符 captchaId
        String captchaId = UUID.randomUUID().toString();
        // 存储验证码到临时变量
        captchaStorage.put(captchaId, text);

        // 返回 JSON 响应，包含 Base64 图片和验证码标识
        return Map.of(
                "captcha", base64Image,
                "captchaId", captchaId
        );
    }

    // 验证验证码
    @PostMapping("/verifyCaptcha")
    public Map<String, Object> verifyCaptcha(@RequestBody Map<String, String> request) {
        String captchaId = request.get("captchaId");
        String userInput = request.get("captchaInput");

        // 从临时变量中取出验证码
        String correctCaptcha = captchaStorage.get(captchaId);

        Map<String, Object> response = Map.of("success", false); // 默认失败

        if (correctCaptcha != null && correctCaptcha.equalsIgnoreCase(userInput)) {
            response = Map.of("success", true);
            // 验证成功后，移除验证码
            captchaStorage.remove(captchaId);
        } else {
            response = Map.of("success", false, "message", "验证码错误或已失效");
        }
        return response;
    }


}
