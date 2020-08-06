package com.tanbin.mall3.interceptor;

import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.exception.UserLoginException;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static com.tanbin.mall3.consts.MallConst.CURRENT_USER;
/**
 * 拦截器
 */
@Slf4j
public class UserLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        log.info("preHandle...............");
        User user = (User) request.getSession().getAttribute(CURRENT_USER);
        if (user == null){
            log.info("user=null");
            //直接做一个全局异常处理
            throw new UserLoginException();
            //return false;//false表示中断流程
            //return ResponseVo.error(ResponseEnum.NEED_LOGIN);
        }
        return true;//true表示继续流程
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
