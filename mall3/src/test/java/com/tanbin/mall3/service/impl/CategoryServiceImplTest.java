package com.tanbin.mall3.service.impl;

import com.tanbin.mall3.Mall3ApplicationTests;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.service.ICategoryService;
import com.tanbin.mall3.vo.CategoryVo;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;
@Slf4j
public class CategoryServiceImplTest extends Mall3ApplicationTests {

    @Autowired
    private ICategoryService categoryService;
    @Test
    public void selectAll() {
        ResponseVo<List<CategoryVo>> responseVo = categoryService.selectAll();
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void findSubCategoryId() {
        Set<Integer> set = new HashSet<>();
        categoryService.findSubCategoryId(100001,set);
        log.info("set={}",set);
    }
}