package com.dahuang.logistics.service;

import com.dahuang.logistics.dto.LoginDTO;
import com.dahuang.logistics.dto.RegisterDTO;
import com.dahuang.logistics.dto.UpdateDTO;
import com.dahuang.logistics.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


public interface UserService {

    void register(RegisterDTO registerDTO);

    boolean login(LoginDTO loginDTO);

    boolean updateUser(UpdateDTO updateDTO);

    boolean selectUserByUserNo(String userNo);

    String uploadAvatar(String userNo, MultipartFile file) throws IOException;

    User getUserInfoByUserNo(String userNo);

    boolean getStatus(String userNo);

    String sendSimpleEmail(String userNo);

    String getEmail(String userNo);

    String getAvatarUrl(String username);
}


