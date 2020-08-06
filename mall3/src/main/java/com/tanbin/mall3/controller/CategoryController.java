package com.tanbin.mall3.controller;

import com.tanbin.mall3.service.ICategoryService;
import com.tanbin.mall3.vo.CategoryVo;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;
    @GetMapping("/categories")
    public ResponseVo<List<CategoryVo>> selectAll(){
        return categoryService.selectAll();
    }
}
