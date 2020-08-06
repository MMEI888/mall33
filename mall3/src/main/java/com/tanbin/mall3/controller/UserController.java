package com.tanbin.mall3.controller;

import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.form.UserLoginForm;
import com.tanbin.mall3.form.UserRegisterForm;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.service.IUserService;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import sun.rmi.runtime.Log;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.Objects;

import static com.tanbin.mall3.consts.MallConst.CURRENT_USER;
import static com.tanbin.mall3.enums.ResponseEnum.PARAM_ERROR;

@Slf4j
@RestController
@RequestMapping
public class UserController {
    @Autowired
    private IUserService userService;
    @PostMapping("/user/register")
    public ResponseVo<User> register(@Valid @RequestBody UserRegisterForm userRegisterForm){
        //参数校验已做了表单统一验证

        User user = new User();
        BeanUtils.copyProperties(userRegisterForm,user);
        return userService.register(user);
    }
    @PostMapping("/user/login")
    public ResponseVo<User> login(@Valid @RequestBody UserLoginForm userLoginForm, HttpSession session){
        //参数校验已做了表单统一验证

        ResponseVo<User> userResponseVo = userService.login(userLoginForm.getUsername(), userLoginForm.getPassword());
        session.setAttribute(CURRENT_USER,userResponseVo.getData());
        log.info("/user/login sessionId = {}",session.getId());
        return userResponseVo;
    }
    //session 保存在内存里，也不安全，，，可以使用：token+redis
    /**
     * 获取用户信息GET
     */
    @GetMapping("/user")
    public ResponseVo<User> userInfo(HttpSession session){
        log.info("/user sessionId = {}",session.getId());
        User user = (User) session.getAttribute(CURRENT_USER);
        return ResponseVo.success(user);
    }
    @PostMapping("/user/logout")
    public ResponseVo logout(HttpSession session){
        log.info("/user/logout sessionId = {}",session.getId());
        //移除session即可退出登录
        session.removeAttribute(CURRENT_USER);
        return ResponseVo.success();
    }
}
