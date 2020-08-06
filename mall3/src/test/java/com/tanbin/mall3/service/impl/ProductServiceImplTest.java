package com.tanbin.mall3.service.impl;

import com.github.pagehelper.PageInfo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.tanbin.mall3.Mall3ApplicationTests;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.service.IProductService;
import com.tanbin.mall3.vo.ProductDetailVo;
import com.tanbin.mall3.vo.ProductVo;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.junit.Assert.*;
@Slf4j
public class ProductServiceImplTest extends Mall3ApplicationTests {
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private IProductService productService;
    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = productService.list(null, 1, 4);
        log.info("responseVo={}",gson.toJson(responseVo));
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void detail() {
        ResponseVo<ProductDetailVo> responseVo = productService.detail(26);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());

    }
}