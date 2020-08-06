package com.tanbin.mall3.service.impl;

import com.tanbin.mall3.Mall3ApplicationTests;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.service.IUserService;
import com.tanbin.mall3.vo.ResponseVo;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;
@Transactional
public class UserServiceImplTest extends Mall3ApplicationTests {

    @Autowired
    private IUserService userService;

    public static final String USERNAME = "jack";
    public static final String PASSWORD = "123456";
    @Before
    public void register() {
        User user = new User(USERNAME, PASSWORD, "jack@qq.com");
        userService.register(user);
    }

    @Test
    public void login() {
        ResponseVo<User> responseVo = userService.login(USERNAME, PASSWORD);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}