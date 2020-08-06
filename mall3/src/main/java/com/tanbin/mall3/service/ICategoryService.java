package com.tanbin.mall3.service;

import com.tanbin.mall3.vo.CategoryVo;
import com.tanbin.mall3.vo.ResponseVo;

import java.util.List;
import java.util.Set;

public interface ICategoryService {
    //这个地方的实现使用到了递归查询
    ResponseVo<List<CategoryVo>> selectAll();

    //该方法查询类目id，包含子类目id以及子字类目id
    void findSubCategoryId(Integer id, Set<Integer> resultSet);
}
