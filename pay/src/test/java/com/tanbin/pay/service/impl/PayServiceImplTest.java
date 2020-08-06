package com.tanbin.pay.service.impl;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import com.tanbin.pay.PayApplicationTests;
import com.tanbin.pay.service.IPayService;
import org.junit.Test;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class PayServiceImplTest extends PayApplicationTests {

    @Autowired
    private IPayService payService;

    @Autowired
    private AmqpTemplate amqpTemplate;
    @Test
    public void create() {
        payService.create("13456132156548", BigDecimal.valueOf(0.01), BestPayTypeEnum.ALIPAY_PC);
    }
    @Test
    public void sendMQMsg(){
        amqpTemplate.convertAndSend("payNotify","world!");
    }
}