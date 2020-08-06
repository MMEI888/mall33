package com.tanbin.pay.service;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.lly835.bestpay.model.PayResponse;
import com.tanbin.pay.pojo.PayInfo;

import java.math.BigDecimal;

public interface IPayService {
    /**
     * 创建/发起支付
     * @param orderId
     * @param amount
     * @param bestPayTypeEnum
     * @return
     */
    PayResponse create(String orderId, BigDecimal amount, BestPayTypeEnum bestPayTypeEnum);

    /**
     * 异步通知处理
     * @param notifyData
     */
    String notifyData(String notifyData);

    /**
     * 查询支付记录
     * @param orderId
     * @return
     */
    PayInfo queryByOrderId(String orderId);
}
