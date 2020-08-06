package com.tanbin.mall3.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tanbin.mall3.Mall3ApplicationTests;
import com.tanbin.mall3.enums.OrderStatusEnum;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.form.CartAddForm;
import com.tanbin.mall3.service.ICartService;
import com.tanbin.mall3.service.IOrderService;
import com.tanbin.mall3.vo.CartVo;
import com.tanbin.mall3.vo.OrderVo;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;

import static org.junit.Assert.*;
@Slf4j
public class OrderServiceImplTest extends Mall3ApplicationTests {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private IOrderService orderService;
    @Autowired
    private ICartService cartService;
    private Integer uid = 1;
    private Integer shippingId = 4;
    public Integer productId = 28;
    @Before
    public void before(){
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, form);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
    @Test
    public void createTest() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
    private ResponseVo<OrderVo> create() {
        ResponseVo<OrderVo> responseVo = orderService.create(uid, shippingId);
        log.info("result={}",gson.toJson(responseVo));
        return responseVo;
    }
    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = orderService.list(uid, 1, 10);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<OrderVo> vo = create();
        ResponseVo<OrderVo> responseVo = orderService.detail(uid,vo.getData().getOrderNo());
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void cancel() {
        ResponseVo<OrderVo> vo = create();
        ResponseVo responseVo = orderService.cancel(uid,vo.getData().getOrderNo());
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}