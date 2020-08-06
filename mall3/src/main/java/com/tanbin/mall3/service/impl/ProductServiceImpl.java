package com.tanbin.mall3.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tanbin.mall3.dao.ProductMapper;
import com.tanbin.mall3.pojo.Product;
import com.tanbin.mall3.service.ICategoryService;
import com.tanbin.mall3.service.IProductService;
import com.tanbin.mall3.vo.ProductDetailVo;
import com.tanbin.mall3.vo.ProductVo;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tanbin.mall3.enums.ProductStatusEnum.DELETE;
import static com.tanbin.mall3.enums.ProductStatusEnum.OFF_SALE;
import static com.tanbin.mall3.enums.ResponseEnum.PRODUCT_DELETE_OR_OFF_SALE;

@Slf4j
@Service
public class ProductServiceImpl implements IProductService {
    @Autowired
    private ICategoryService categoryService;
    @Autowired
    private ProductMapper productMapper;
    @Override
    public ResponseVo<PageInfo> list(Integer categoryId, Integer pageNum, Integer pageSize) {
        Set<Integer> categoryIdSet = new HashSet<>();
        if (categoryId != null){
            categoryService.findSubCategoryId(categoryId,categoryIdSet);
            //上面的查询子类目id的方法并没把自身加进去，所以还要把自身加进去
            categoryIdSet.add(categoryId);
        }
        //引入插件依赖以后，只要这一行代码就可以使用分页功能了
        PageHelper.startPage(pageNum,pageSize);

        List<Product> productList = productMapper.selectByCategoryIdSet(categoryIdSet);
        List<ProductVo> productVoList = productList.stream()
                //这里是将通过categoryId查到的product对象转换为productVo
                .map(e -> {
                    ProductVo productVo = new ProductVo();
                    BeanUtils.copyProperties(e, productVo);
                    return productVo;
                }).collect(Collectors.toList());
        PageInfo pageInfo = new PageInfo<>(productVoList);//这里放的是从数据库中查到的数据:Product对象
        //pageInfo.setList(productVoList);//这里则是要返回的vo对象：ProductVo对象
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<ProductDetailVo> detail(Integer productId) {
        Product product = productMapper.selectByPrimaryKey(productId);
        if (product.getStatus().equals(OFF_SALE.getCode()) || product.getStatus().equals(DELETE.getCode())){
            return ResponseVo.error(PRODUCT_DELETE_OR_OFF_SALE);
        }
        ProductDetailVo productDetailVo = new ProductDetailVo();
        BeanUtils.copyProperties(product,productDetailVo);

        //对库存进行“遮盖”处理
        productDetailVo.setStock(product.getStock() > 100 ? 100 : product.getStock());

        return ResponseVo.success(productDetailVo);
    }
}
