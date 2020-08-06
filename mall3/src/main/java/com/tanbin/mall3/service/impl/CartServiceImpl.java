package com.tanbin.mall3.service.impl;

import com.google.gson.Gson;
import com.sun.org.apache.bcel.internal.generic.IF_ACMPEQ;
import com.tanbin.mall3.dao.ProductMapper;
import com.tanbin.mall3.enums.ProductStatusEnum;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.form.CartAddForm;
import com.tanbin.mall3.form.CartUpdateForm;
import com.tanbin.mall3.pojo.Cart;
import com.tanbin.mall3.pojo.Product;
import com.tanbin.mall3.service.ICartService;
import com.tanbin.mall3.vo.CartProductVo;
import com.tanbin.mall3.vo.CartVo;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CartServiceImpl implements ICartService {

    private final static String CART_REDIS_KEY_TEMPLATE = "cart_%d";
    Integer quantity = 1;
    private Gson gson = new Gson();

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private StringRedisTemplate redisTemplate;
    @Override
    public ResponseVo<CartVo> add(Integer uid,CartAddForm cartAddForm) {
        //1、判断商品是否存在
        Product product = productMapper.selectByPrimaryKey(cartAddForm.getProductId());
        if (product == null){
            return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST);
        }
        //2、判断商品在售状态
        if (product.getStatus().equals(ProductStatusEnum.OFF_SALE.getCode())){
            return ResponseVo.error(ResponseEnum.PRODUCT_DELETE_OR_OFF_SALE);
        }
        //3、判断商品库存是否充足
        if (product.getStock() <= 0){
            return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR);
        }
        //写入到redis。自定义redis key 为：cart_1的形式

        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Cart cart;
        String value = opsForHash.get(redisKey, String.valueOf(product.getId()));
        if (StringUtils.isEmpty(value)){
            //没有该商品，要新增
            cart = new Cart(product.getId(),quantity, cartAddForm.getSelected());
        }else {
            //已有，则数量+1
            cart = gson.fromJson(value,Cart.class);
            cart.setQuantity(cart.getQuantity() + quantity);
        }
        opsForHash.put(redisKey,String.valueOf(product.getId()),gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> list(Integer uid) {
//        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
//        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
//        Map<String, String> entries = opsForHash.entries(redisKey);

        CartVo cartVo = new CartVo();
        ArrayList<CartProductVo> cartProductVoList = new ArrayList<>();

        Boolean selectAll = true;
        Integer cartTotalQuantity = 0;
        BigDecimal cartTotalPrice = BigDecimal.ZERO;

        //优化：改善在for循环中使用sql语句

        //先获取购物车
        List<Cart> cartList = listForCart(uid).stream()
                .filter(Cart::getProductSelected)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(cartList)){
            return ResponseVo.error(ResponseEnum.CART_SELECTED_IS_EMPTY);
        }
        //再从购物车里拿到所有商品的id：productIdSet
        Set<Integer> productIdSet = cartList.stream()
                .map(Cart::getProductId)
                .collect(Collectors.toSet());
        //再根据这productIdSet从数据库一次性查询商品
        List<Product> productList = productMapper.selectByProductIdSet(productIdSet);
        //再将查到的productList转换称以productId为key，以product对象为值的Map形式，方便遍历购物车时比对商品是否正确
        Map<Integer, Product> map = productList.stream()
                .collect(Collectors.toMap(Product::getId, product -> product));
        //再遍历购物车
        for (Cart cart : cartList) {
            Product product = map.get(cart.getProductId());
            if (product != null){
                CartProductVo cartProductVo = new CartProductVo(
                        cart.getProductId(),
                        cart.getQuantity(),
                        product.getName(),
                        product.getSubtitle(),
                        product.getMainImage(),
                        product.getPrice(),
                        product.getStatus(),
                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
                        product.getStock(),
                        cart.getProductSelected());
                cartProductVoList.add(cartProductVo);
                //有一个没选中那就不叫全选
                if (!cart.getProductSelected()){
                    selectAll = false;
                }
                //只计算选中的商品的价格
                if (cart.getProductSelected()){
                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
                }
            }
            cartTotalQuantity += cart.getQuantity();
        }



//        for (Map.Entry<String, String> entry : entries.entrySet()) {
//            Integer productId = Integer.valueOf(entry.getKey());
//            Cart cart = gson.fromJson(entry.getValue(), Cart.class);
//
//            //TODO for循环中用了sql语句查询，需要优化，用in
//            Product product = productMapper.selectByPrimaryKey(productId);
//            if (product != null){
//                CartProductVo cartProductVo = new CartProductVo(
//                        productId,
//                        cart.getQuantity(),
//                        product.getName(),
//                        product.getSubtitle(),
//                        product.getMainImage(),
//                        product.getPrice(),
//                        product.getStatus(),
//                        product.getPrice().multiply(BigDecimal.valueOf(cart.getQuantity())),
//                        product.getStock(),
//                        cart.getProductSelected());
//                cartProductVoList.add(cartProductVo);
//                //有一个没选中那就不叫全选
//                if (!cart.getProductSelected()){
//                    selectAll = false;
//                }
//                //只计算选中的商品的价格
//                if (cart.getProductSelected()){
//                    cartTotalPrice = cartTotalPrice.add(cartProductVo.getProductTotalPrice());
//                }
//            }
//            cartTotalQuantity += cart.getQuantity();
//        }
        cartVo.setSelectAll(selectAll);
        cartVo.setCartTotalPrice(cartTotalPrice);
        cartVo.setCartTotalQuantity(cartTotalQuantity);
        cartVo.setCartProductVoList(cartProductVoList);
        return ResponseVo.success(cartVo);
    }

    @Override
    public ResponseVo<CartVo> update(Integer uid, Integer productId, CartUpdateForm cartUpdateForm) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)){
            //没有该商品，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        //已有，更新
        Cart cart = gson.fromJson(value,Cart.class);
        if (cartUpdateForm.getQuantity() != null && cartUpdateForm.getQuantity() >= 0){
             cart.setQuantity(cartUpdateForm.getQuantity());
        }
        if (cartUpdateForm.getSelected() != null){
            cart.setProductSelected(cartUpdateForm.getSelected());
        }
        opsForHash.put(redisKey,String.valueOf(productId),gson.toJson(cart));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> delete(Integer uid, Integer productId) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);

        String value = opsForHash.get(redisKey, String.valueOf(productId));
        if (StringUtils.isEmpty(value)){
            //没有该商品，报错
            return ResponseVo.error(ResponseEnum.CART_PRODUCT_NOT_EXIST);
        }
        opsForHash.delete(redisKey,String.valueOf(productId));
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> selectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(true);
            opsForHash.put(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<CartVo> unSelectAll(Integer uid) {
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        for (Cart cart : listForCart(uid)) {
            cart.setProductSelected(false);
            opsForHash.put(redisKey,String.valueOf(cart.getProductId()),gson.toJson(cart));
        }
        return list(uid);
    }

    @Override
    public ResponseVo<Integer> sum(Integer uid) {
        Integer sum = listForCart(uid).stream()
                .map(Cart::getQuantity)
                .reduce(0, Integer::sum);
        return ResponseVo.success(sum);

    }
    //获取购物车列表
    public List<Cart> listForCart(Integer uid){
        HashOperations<String, String, String> opsForHash = redisTemplate.opsForHash();
        String redisKey = String.format(CART_REDIS_KEY_TEMPLATE, uid);
        Map<String, String> entries = opsForHash.entries(redisKey);
        List<Cart> cartList = new ArrayList<>();
        for (Map.Entry<String, String> entry : entries.entrySet()) {
            cartList.add(gson.fromJson(entry.getValue(),Cart.class));
        }
        return cartList;
    }
}
