package com.tanbin.mall3.service;

import com.tanbin.mall3.form.CartAddForm;
import com.tanbin.mall3.form.CartUpdateForm;
import com.tanbin.mall3.pojo.Cart;
import com.tanbin.mall3.vo.CartVo;
import com.tanbin.mall3.vo.ResponseVo;

import java.util.List;

public interface ICartService {
    ResponseVo<CartVo> add(Integer uid,CartAddForm cartAddForm);
    ResponseVo<CartVo> list(Integer uid);
    ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm);
    ResponseVo<CartVo> delete(Integer uid, Integer productId);
    ResponseVo<CartVo> selectAll(Integer uid);
    ResponseVo<CartVo> unSelectAll(Integer uid);
    ResponseVo<Integer> sum(Integer uid);
    List<Cart> listForCart(Integer uid);
}
