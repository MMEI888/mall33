package com.tanbin.mall3.controller;

import com.tanbin.mall3.form.ShippingForm;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.service.IShippingService;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Map;

import static com.tanbin.mall3.consts.MallConst.CURRENT_USER;

@RestController
public class ShippingController {
    @Autowired
    private IShippingService shippingService;
    @PostMapping("/shippings")
    public ResponseVo<Map<String,Integer>> add(HttpSession session,
                                               @Valid @RequestBody ShippingForm form){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.add(user.getId(), form);
    }
    @DeleteMapping("/shippings/{shippingId}")
    public ResponseVo delete(@PathVariable Integer shippingId,HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.delete(user.getId(),shippingId);
    }
    @PutMapping("/shippings/{shippingId}")
    public ResponseVo update(HttpSession session,
                             @PathVariable Integer shippingId,
                             @Valid @RequestBody ShippingForm form){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.update(user.getId(),shippingId,form);
    }
    @GetMapping("/shippings")
    public ResponseVo list(HttpSession session,
                           @RequestParam(required = false,defaultValue = "1") Integer pageNum,
                           @RequestParam(required = false,defaultValue = "10") Integer pageSize){
        User user = (User) session.getAttribute(CURRENT_USER);
        return shippingService.list(user.getId(),pageNum,pageSize);
    }
}
