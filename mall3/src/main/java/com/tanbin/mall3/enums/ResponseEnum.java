package com.tanbin.mall3.enums;

import lombok.Getter;

@Getter
public enum ResponseEnum {
    ERROR(-1,"服务端异常"),
    SUCCESS(0,"成功"),
    PASSWORD_ERROR(1,"密码错误"),
    USERNAME_EXIST(2,"用户名已存在"),
    PARAM_ERROR(3,"参数错误"),
    EMAIL_EXIST(4,"邮箱已存在"),
    NEED_LOGIN(10,"用户未登录，请先登录"),
    USERNAME_OR_PASSWORD_ERROR(11,"用户名或密码错误"),
    PRODUCT_DELETE_OR_OFF_SALE(12,"商品删除或已下架"),
    PRODUCT_NOT_EXIST(13,"商品不存在"),
    PRODUCT_STOCK_ERROR(14,"商品库存不足"),
    CART_PRODUCT_NOT_EXIST(15,"购物车无此商品"),
    DELETE_SHIPPING_FAIL(16,"删除收货地址失败"),
    UPDATE_SHIPPING_FAIL(17,"更新收货地址失败"),
    SHIPPING_NOT_EXIST(18,"收货地址不存在"),
    CART_SELECTED_IS_EMPTY(19,"请选中商品后下单"),
    ORDER_NOT_EXIST(20,"订单不存在"),
    ORDER_STATUS_ERROR(21,"订单状态有误"),
        ;
    Integer code;
    String desc;

    ResponseEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
