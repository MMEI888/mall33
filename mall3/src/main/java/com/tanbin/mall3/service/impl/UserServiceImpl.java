package com.tanbin.mall3.service.impl;

import com.tanbin.mall3.dao.UserMapper;
import com.tanbin.mall3.enums.RoleEnum;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.service.IUserService;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.nio.charset.StandardCharsets;

import static com.tanbin.mall3.enums.ResponseEnum.*;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserMapper userMapper;

    /**
     * 注册
     * @param user
     * @return
     */
    @Override
    public ResponseVo<User> register(User user) {
        //error();
        //username不能重复
        int countByUsername = userMapper.countByUsername(user.getUsername());
        if (countByUsername > 0){
            return ResponseVo.error(USERNAME_EXIST);
        }
        //email不能重复
        int countByEmail = userMapper.countByEmail(user.getEmail());
        if (countByEmail > 0){
            return ResponseVo.error(EMAIL_EXIST);
        }
        user.setRole(RoleEnum.CUSTOMER.getCode());
        //MD5摘要加密，spring中已有此工具类，直接使用即可
        user.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes(StandardCharsets.UTF_8)));
        //写入数据库
        int row = userMapper.insertSelective(user);
        if (row == 0){
            return ResponseVo.error(ERROR);
        }
        return ResponseVo.success();
    }

    /**
     * 登录
     * @param username
     * @param password
     * @return
     */
    @Override
    public ResponseVo<User> login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user == null){
            //用户名不存在(但是返回的是“用户名或密码错误”)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        if (!user.getPassword().equalsIgnoreCase(DigestUtils.md5DigestAsHex(password.getBytes(StandardCharsets.UTF_8)))) {
            //密码错误(但是返回的是“用户名或密码错误”)
            return ResponseVo.error(USERNAME_OR_PASSWORD_ERROR);
        }
        user.setPassword("");
        return ResponseVo.success(user);
    }







//    private void error(){
//        throw new RuntimeException("这里是举例，模仿异常，返回json");
//    }
}
