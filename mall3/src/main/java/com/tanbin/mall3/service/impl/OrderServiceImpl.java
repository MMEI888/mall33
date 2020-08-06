package com.tanbin.mall3.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.tanbin.mall3.dao.OrderItemMapper;
import com.tanbin.mall3.dao.OrderMapper;
import com.tanbin.mall3.dao.ProductMapper;
import com.tanbin.mall3.dao.ShippingMapper;
import com.tanbin.mall3.enums.OrderStatusEnum;
import com.tanbin.mall3.enums.PaymentTypeEnum;
import com.tanbin.mall3.enums.ProductStatusEnum;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.pojo.*;
import com.tanbin.mall3.service.ICartService;
import com.tanbin.mall3.service.IOrderService;
import com.tanbin.mall3.vo.OrderItemVo;
import com.tanbin.mall3.vo.OrderVo;
import com.tanbin.mall3.vo.ResponseVo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;
@Service
public class OrderServiceImpl implements IOrderService {
    @Autowired
    private ShippingMapper shippingMapper;
    @Autowired
    private ICartService cartService;
    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private OrderItemMapper orderItemMapper;
    @Override
    @Transactional
    public ResponseVo<OrderVo> create(Integer uid, Integer shippingId) {
        //收货地址校验
        Shipping shipping = shippingMapper.selectByUidAndShippingId(uid, shippingId);
        if (shipping == null){
            return ResponseVo.error(ResponseEnum.SHIPPING_NOT_EXIST);
        }
        //先获取购物车
        List<Cart> cartList = cartService.listForCart(uid).stream()
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
        List<OrderItem> orderItemList = new ArrayList<>();
        Long orderNo = generateOrderNo();
        //遍历购物车
        for (Cart cart : cartList) {
            Product product = map.get(cart.getProductId());
            //判断商品是否存在
            if (product == null){
                return ResponseVo.error(ResponseEnum.PRODUCT_NOT_EXIST,
                        "商品不存在. productId=" + cart.getProductId());
            }
            //判断商品的在售状态
            if (!ProductStatusEnum.ON_SALE.getCode().equals(product.getStatus())){
                return ResponseVo.error(ResponseEnum.PRODUCT_DELETE_OR_OFF_SALE,
                        "商品不是在售状态,"+ product.getName());
            }
            //判断商品库存是否充足
            if (product.getStock() < cart.getQuantity()){
                return ResponseVo.error(ResponseEnum.PRODUCT_STOCK_ERROR,
                        "商品库存不足." + product.getName());
            }
            //减库存
            product.setStock(product.getStock()-cart.getQuantity());
            int row = productMapper.updateByPrimaryKeySelective(product);
            if (row <= 0){
                return ResponseVo.error(ResponseEnum.ERROR);
            }
            OrderItem orderItem = buildOrderItem(uid, orderNo, cart.getQuantity(), product);
            orderItemList.add(orderItem);
        }
        //计算总价，只计算选中的商品（本项目中没有涉及到优惠券）
        //生成订单，入库：这里有两张表order和order_item,要用到事务
        Order order = buildOrder(uid, orderNo, shippingId, orderItemList);
        int rowOrder = orderMapper.insertSelective(order);
        if (rowOrder <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        int rowOrderItem = orderItemMapper.batchInsert(orderItemList);
        if (rowOrderItem <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        //更新购物车
        for (Cart cart : cartList) {
            cartService.delete(uid,cart.getProductId());
        }
        //构造orderVo对象并返回
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    private OrderVo buildOrderVo(Order order, List<OrderItem> orderItemList, Shipping shipping) {
        OrderVo orderVo = new OrderVo();
        BeanUtils.copyProperties(order,orderVo);
        List<OrderItemVo> orderItemVoList = orderItemList.stream()
                .map(e -> {
                    OrderItemVo orderItemVo = new OrderItemVo();
                    BeanUtils.copyProperties(e, orderItemVo);
                    return orderItemVo;
                }).collect(Collectors.toList());
        orderVo.setOrderItemVoList(orderItemVoList);
        if (shipping != null){
            orderVo.setShippingId(shipping.getId());
            orderVo.setShippingVo(shipping);
        }
        return orderVo;
    }

    /**
     * 思路：
     * 首先根据uid丛数据库中拿到所有order
     * 再从orderList中拿到所有的订单号  ：orderNoSet
     * 再根据orderNoSet从orderItem表中拿到所有的orderItem，并转换为map
     * 再从orderList中拿到所有的收货地址shippingIdSet
     * 再根据shippingIdSet从shipping表中拿到所有的shipping，并转换为map
     * 再遍历orderList，从中构造出orderVo，注意其中的参数，要保证能拿到订单号下所对应的订单详情。
     * @param uid
     * @param pageNum
     * @param pageSize
     * @return
     */

    @Override
    public ResponseVo<PageInfo> list(Integer uid, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum,pageSize);
        List<Order> orderList = orderMapper.selectByUid(uid);
        Set<Long> orderNoSet = orderList.stream()
                .map(Order::getOrderNo)
                .collect(Collectors.toSet());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Map<Long, List<OrderItem>> orderItemMap = orderItemList.stream()
                .collect(Collectors.groupingBy(OrderItem::getOrderNo));
        Set<Integer> shippingIdSet = orderList.stream()
                .map(Order::getShippingId)
                .collect(Collectors.toSet());
        List<Shipping> shippingList = shippingMapper.selectByIdSet(shippingIdSet);
        Map<Integer, Shipping> shippingMap = shippingList.stream()
                .collect(Collectors.toMap(Shipping::getId, shipping -> shipping));

        List<OrderVo> orderVoList = new ArrayList<>();
        for (Order order : orderList) {
            OrderVo orderVo = buildOrderVo(order,
                    orderItemMap.get(order.getOrderNo()),
                    shippingMap.get(order.getShippingId()));
            orderVoList.add(orderVo);
        }
        PageInfo<OrderVo> pageInfo = new PageInfo<>();
        pageInfo.setList(orderVoList);
        return ResponseVo.success(pageInfo);
    }

    @Override
    public ResponseVo<OrderVo> detail(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        HashSet<Long> orderNoSet = new HashSet<>();
        orderNoSet.add(order.getOrderNo());
        List<OrderItem> orderItemList = orderItemMapper.selectByOrderNoSet(orderNoSet);
        Shipping shipping = shippingMapper.selectByPrimaryKey(order.getShippingId());
        OrderVo orderVo = buildOrderVo(order, orderItemList, shipping);
        return ResponseVo.success(orderVo);
    }

    @Override
    public ResponseVo cancel(Integer uid, Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null || !order.getUserId().equals(uid)){
            return ResponseVo.error(ResponseEnum.ORDER_NOT_EXIST);
        }
        //只有未付款可以取消订单，这里要看具体业务而定
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            return ResponseVo.error(ResponseEnum.ORDER_STATUS_ERROR);
        }
        order.setStatus(OrderStatusEnum.CANCELED.getCode());
        order.setCloseTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            return ResponseVo.error(ResponseEnum.ERROR);
        }
        return ResponseVo.success();
    }

    @Override
    public void paid(Long orderNo) {
        Order order = orderMapper.selectByOrderNo(orderNo);
        if (order == null){
            throw new RuntimeException(ResponseEnum.ORDER_NOT_EXIST.getDesc() + "订单id：" + orderNo);
        }
        //只有【未付款】可以修改为【已付款】，这里要看具体业务而定
        if (!order.getStatus().equals(OrderStatusEnum.NO_PAY.getCode())){
            throw new RuntimeException(ResponseEnum.ORDER_STATUS_ERROR.getDesc() + "订单id：" + orderNo);
        }
        order.setStatus(OrderStatusEnum.PAID.getCode());
        order.setPaymentTime(new Date());
        int row = orderMapper.updateByPrimaryKeySelective(order);
        if (row <= 0){
            throw new RuntimeException("将订单更新为已支付状态失败订单id：" + orderNo);
        }
    }


    /**
     * 随机生成订单号
     * @return
     */
    private Long generateOrderNo(){
        return System.currentTimeMillis() + new Random().nextInt(999);
    }

    /**
     * 构造OrderItem表，也就是写入订单详情
     * @param uid
     * @param orderNo
     * @param quantity
     * @param product
     * @return
     */
    private OrderItem buildOrderItem(Integer uid, Long orderNo, Integer quantity, Product product) {
        OrderItem item = new OrderItem();
        item.setUserId(uid);
        item.setOrderNo(orderNo);
        item.setProductId(product.getId());
        item.setProductName(product.getName());
        item.setProductImage(product.getMainImage());
        item.setCurrentUnitPrice(product.getPrice());
        item.setQuantity(quantity);
        item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(quantity)));
        return item;
    }

    /**
     * 构造Order表，也就是写入订单主表
     * @param uid
     * @param orderNo
     * @param shippingId
     * @param orderItemList
     * @return
     */
    private Order buildOrder(Integer uid,
                             Long orderNo,
                             Integer shippingId,
                             List<OrderItem> orderItemList) {
        //对订单详情表中的总价进行累加
        BigDecimal payment = orderItemList.stream()
                .map(OrderItem::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(uid);
        order.setShippingId(shippingId);
        order.setPayment(payment);
        order.setPaymentType(PaymentTypeEnum.PAY_ONLINE.getCode());
        order.setPostage(0);
        order.setStatus(OrderStatusEnum.NO_PAY.getCode());
        return order;
    }
}
