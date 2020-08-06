package com.tanbin.mall3.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CartVo {
    private List<CartProductVo> cartProductVoList;
    private Boolean selectAll;
    private BigDecimal cartTotalPrice;
    private Integer cartTotalQuantity;
}
