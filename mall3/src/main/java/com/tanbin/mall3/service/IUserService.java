package com.tanbin.mall3.service;

import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.web.bind.annotation.ResponseBody;

public interface IUserService {
    //注册
    ResponseVo<User> register(User user);
    //登录
    ResponseVo<User> login(String username,String password);
}
