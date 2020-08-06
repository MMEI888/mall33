package com.tanbin.mall3.dao;

import com.tanbin.mall3.pojo.Order;
import com.tanbin.mall3.pojo.OrderItem;
import com.tanbin.mall3.pojo.Product;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Set;

public interface OrderMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Order record);

    int insertSelective(Order record);

    Order selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Order record);

    int updateByPrimaryKey(Order record);

    List<Order> selectByUid(Integer uid);

    Order selectByOrderNo(Long orderNo);
}