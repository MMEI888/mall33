package com.tanbin.mall3.vo;

import lombok.Data;

import java.util.List;

@Data
public class CategoryVo {
    private Integer id;

    private Integer parentId;

    private String name;

    private Integer sortOrder;

    private List<CategoryVo> subCategories;//返回值也是CategoryVo，也就是递归查询。
}
