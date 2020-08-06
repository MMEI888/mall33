package com.tanbin.mall3.controller;

import com.github.pagehelper.PageInfo;
import com.tanbin.mall3.form.OrderCreateForm;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.service.IOrderService;
import com.tanbin.mall3.vo.OrderVo;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.tanbin.mall3.consts.MallConst.CURRENT_USER;

@RestController
public class OrderController {
    @Autowired
    private IOrderService orderService;
    @PostMapping("/orders")
    public ResponseVo<OrderVo> create(@Valid @RequestBody OrderCreateForm form,
                                      HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.create(user.getId(), form.getShippingId());
    }
    @GetMapping("/orders")
    public ResponseVo<PageInfo> list(@RequestParam(defaultValue = "1") Integer pageNum,
                                     @RequestParam(defaultValue = "10") Integer pageSize,
                                     HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.list(user.getId(),pageNum,pageSize);
    }
    @GetMapping("/orders/{orderNo}")
    public ResponseVo<OrderVo> detail(@PathVariable Long orderNo, HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.detail(user.getId(),orderNo);
    }
    @PutMapping("/orders/{orderNo}")
    public ResponseVo cancel(@PathVariable Long orderNo, HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return orderService.cancel(user.getId(),orderNo);
    }
}
