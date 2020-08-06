package com.tanbin.pay.service.impl;

import com.google.gson.Gson;
import com.lly835.bestpay.config.WxPayConfig;
import com.lly835.bestpay.enums.BestPayPlatformEnum;
import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.enums.OrderStatusEnum;
import com.lly835.bestpay.model.PayRequest;
import com.lly835.bestpay.model.PayResponse;
import com.lly835.bestpay.service.BestPayService;
import com.tanbin.pay.dao.PayInfoMapper;
import com.tanbin.pay.enums.PayPlatformEnum;
import com.tanbin.pay.pojo.PayInfo;
import com.tanbin.pay.service.IPayService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class PayServiceImpl implements IPayService {
    public static final String QUEUE_PAY_NOTIFY = "payNotify";
    @Autowired
    private BestPayService bestPayService;
    @Autowired
    private PayInfoMapper payInfoMapper;
    @Autowired
    private AmqpTemplate amqpTemplate;

    @Override
    public PayResponse create(String orderId, BigDecimal amount,BestPayTypeEnum bestPayTypeEnum) {
        //写入数据库
        PayInfo payInfo = new PayInfo(Long.parseLong(orderId),
                PayPlatformEnum.getByBestPayTypeEnum(bestPayTypeEnum).getCode(),
                OrderStatusEnum.NOTPAY.name(),
                amount);
        payInfoMapper.insertSelective(payInfo);

        PayRequest request = new PayRequest();
        request.setOrderName("6288054-最好的支付sdk");
        request.setOrderId(orderId);
        request.setOrderAmount(amount.doubleValue());
        request.setPayTypeEnum(bestPayTypeEnum);
        PayResponse response = bestPayService.pay(request);
        log.info("发起支付response={}",response);
        return response;
    }

    /**
     * 异步通知处理
     * @param notifyData
     */
    @Override
    public String notifyData(String notifyData) {
        //1.签名校验
        PayResponse payResponse = bestPayService.asyncNotify(notifyData);
        log.info("异步通知payResponse={}",payResponse);
        //2.金额校验（从数据库查订单）
        PayInfo payInfo = payInfoMapper.selectByOrderNo(Long.parseLong(payResponse.getOrderId()));
        if (payInfo == null){
            //这里情况视为严重的，此时应该发出告警！
            throw new RuntimeException("通过orderNo查询到的结果是null");
        }
        //判断订单支付状态
        if (!payInfo.getPlatformStatus().equals(OrderStatusEnum.SUCCESS.name())){
            if (payInfo.getPayAmount().compareTo(BigDecimal.valueOf(payResponse.getOrderAmount()))!= 0){
                //这里情况视为严重的，此时应该发出告警！
                throw new RuntimeException("异步通知中的金额和数据库里的不一致，orderNo="+payResponse.getOrderId());
            }
            //3.修改订单的支付状态
            payInfo.setPlatformStatus(OrderStatusEnum.SUCCESS.name());
            payInfo.setPlatformNumber(payResponse.getOutTradeNo());
            payInfo.setUpdateTime(null);
            payInfoMapper.updateByPrimaryKeySelective(payInfo);
        }
        //TODO pay发送MQ消息，mall接收MQ消息
        amqpTemplate.convertAndSend(QUEUE_PAY_NOTIFY,new Gson().toJson(payInfo));

        if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.WX){
            //4.告诉微信不要再重复通知
            return "<xml>\n" +
                    "  <return_code><![CDATA[SUCCESS]]></return_code>\n" +
                    "  <return_msg><![CDATA[OK]]></return_msg>\n" +
                    "</xml>\n";
        } else if (payResponse.getPayPlatformEnum() == BestPayPlatformEnum.ALIPAY){
            //4.告诉支付宝不要再重复通知
            return "success";
        }
        throw new RuntimeException("异步通知中错误的支付平台");
    }

    @Override
    public PayInfo queryByOrderId(String orderId) {
        return payInfoMapper.selectByOrderNo(Long.parseLong(orderId));
    }
}
