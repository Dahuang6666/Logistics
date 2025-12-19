package com.dahuang.logistics.mapper;
import com.dahuang.logistics.dto.LoginDTO;
import com.dahuang.logistics.dto.UpdateDTO;
import com.dahuang.logistics.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("select password from school_backend_manage.user where user_no=#{userNo} and role=#{role}")
    String  selectPasswordByUserNo(LoginDTO loginDTO);

    int insert(User user);

    @Insert("insert into school_backend_manage.student_dormitory_info (user_no) values (#{userNo})")
    void insertUserNo(String userNo);
    @Select("select * from school_backend_manage.user where user_no=#{userNo}")
    Integer selectUserByUserNo(String userNo);

    int updateUser(UpdateDTO updateDTO);

    @Update("UPDATE school_backend_manage.user SET avatar_url = #{avatarUrl} WHERE user_no = #{userNo}")
    void updateAvatar(@Param("userNo") String userNo, @Param("avatarUrl") String avatarUrl);

    @Select("SELECT * FROM school_backend_manage.user WHERE user_no = #{userNo}")
    User getUserByUserNo(String userNo);
    @Select("select status from school_backend_manage.user where user_no=#{userNo}")
    Integer getStatus(String userNo);
}
