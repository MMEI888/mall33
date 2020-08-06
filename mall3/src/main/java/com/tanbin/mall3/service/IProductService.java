package com.tanbin.mall3.service;

import com.github.pagehelper.PageInfo;
import com.tanbin.mall3.vo.ProductDetailVo;
import com.tanbin.mall3.vo.ProductVo;
import com.tanbin.mall3.vo.ResponseVo;

import java.util.List;

public interface IProductService {
    ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize);
    ResponseVo<ProductDetailVo> detail(Integer productId);
}
