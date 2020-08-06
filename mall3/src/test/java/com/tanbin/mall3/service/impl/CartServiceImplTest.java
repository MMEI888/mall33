package com.tanbin.mall3.service.impl;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tanbin.mall3.Mall3ApplicationTests;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.form.CartAddForm;
import com.tanbin.mall3.form.CartUpdateForm;
import com.tanbin.mall3.service.ICartService;
import com.tanbin.mall3.service.ICategoryService;
import com.tanbin.mall3.vo.CartVo;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;
@Slf4j
public class CartServiceImplTest extends Mall3ApplicationTests {

    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private Integer productId = 28;
    private Integer uid = 1;
    @Autowired
    private ICartService cartService;
    @Test
    public void add() {
        CartAddForm form = new CartAddForm();
        form.setProductId(productId);
        form.setSelected(true);
        ResponseVo<CartVo> responseVo = cartService.add(uid, form);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
    @Test
    public void list(){
        ResponseVo<CartVo> responseVo = cartService.list(uid);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void update() {
        CartUpdateForm form = new CartUpdateForm();
        form.setQuantity(10);
        form.setSelected(false);
        ResponseVo<CartVo> responseVo = cartService.update(uid, productId, form);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void delete() {
        ResponseVo<CartVo> responseVo = cartService.delete(uid, productId);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void selectAll(){
        ResponseVo<CartVo> responseVo = cartService.selectAll(uid);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
    @Test
    public void unSelectAll(){
        ResponseVo<CartVo> responseVo = cartService.unSelectAll(uid);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
    @Test
    public void sum(){
        ResponseVo<Integer> responseVo = cartService.sum(uid);
        log.info("result={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}