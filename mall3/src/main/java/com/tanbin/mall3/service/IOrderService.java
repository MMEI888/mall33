package com.tanbin.mall3.service;

import com.github.pagehelper.PageInfo;
import com.tanbin.mall3.vo.OrderVo;
import com.tanbin.mall3.vo.ResponseVo;

public interface IOrderService {
    ResponseVo<OrderVo> create(Integer uid,Integer shippingId);
    ResponseVo<PageInfo> list(Integer uid,Integer pageNum,Integer pageSize);
    ResponseVo<OrderVo> detail(Integer uid,Long orderNo);
    ResponseVo cancel(Integer uid,Long orderNo);
    void paid(Long orderNo);
}
