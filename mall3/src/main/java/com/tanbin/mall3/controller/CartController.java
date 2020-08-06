package com.tanbin.mall3.controller;

import com.tanbin.mall3.consts.MallConst;
import com.tanbin.mall3.form.CartAddForm;
import com.tanbin.mall3.form.CartUpdateForm;
import com.tanbin.mall3.pojo.User;
import com.tanbin.mall3.service.ICartService;
import com.tanbin.mall3.vo.CartVo;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.tanbin.mall3.consts.MallConst.CURRENT_USER;

@RestController
public class CartController {
    @Autowired
    private ICartService cartService;

    @GetMapping("/carts")
    public ResponseVo<CartVo> list(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.list(user.getId());
    }

    @PostMapping("/carts")
    public ResponseVo<CartVo> add(@Valid @RequestBody CartAddForm cartAddForm,
                                  HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.add(user.getId(),cartAddForm);
    }
    @PutMapping("/carts/{productId}")
    public ResponseVo<CartVo> update(@Valid @RequestBody CartUpdateForm form,
                                     @PathVariable Integer productId,
                                     HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.update(user.getId(),productId,form);
    }
    @DeleteMapping("/carts/{productId}")
    public ResponseVo<CartVo> delete(@PathVariable Integer productId,
                                     HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.delete(user.getId(),productId);
    }
    @PutMapping("/carts/selectAll")
    public ResponseVo<CartVo> selectAll(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.selectAll(user.getId());
    }
    @PutMapping("/carts/unSelectAll")
    public ResponseVo<CartVo> unSelectAll(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.unSelectAll(user.getId());
    }
    @GetMapping("/carts/products/sum")
    public ResponseVo<Integer> sum(HttpSession session){
        User user = (User) session.getAttribute(CURRENT_USER);
        return cartService.sum(user.getId());
    }
}
