package com.tanbin.mall3.listener;

import com.google.gson.Gson;
import com.tanbin.mall3.pojo.PayInfo;
import com.tanbin.mall3.service.IOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 关于PayInfo，正确姿势：pay项目提供client.jar，mall3项目引入jar包
 */

@Component
@Slf4j
@RabbitListener(queues = "payNotify")
public class PayMsgListener {
    @Autowired
    private IOrderService orderService;
    @RabbitHandler
    public void process(String msg){
        log.info("[接受到消息]：  {}",msg);
        PayInfo payInfo = new Gson().fromJson(msg, PayInfo.class);
        if (payInfo.getPlatformStatus().equals("SUCCESS")){
            orderService.paid(payInfo.getOrderNo());
        }

    }
}
