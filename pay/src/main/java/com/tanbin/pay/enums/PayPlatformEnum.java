package com.tanbin.pay.enums;

import com.lly835.bestpay.enums.BestPayTypeEnum;
import lombok.Getter;

@Getter
public enum PayPlatformEnum {
    ALIPAY(1),
    WX(2),
    ;
    Integer code;

    PayPlatformEnum(Integer code) {
        this.code = code;
    }
    public static PayPlatformEnum getByBestPayTypeEnum(BestPayTypeEnum bestPayTypeEnum){
//        if (bestPayTypeEnum.getPlatform().name().equals(PayPlatformEnum.ALIPAY.name())){
//            return PayPlatformEnum.ALIPAY;
//        }else if (bestPayTypeEnum.getPlatform().name().equals(PayPlatformEnum.WX.name())){
//            return PayPlatformEnum.WX;
//        }
        //换一种写法
        for (PayPlatformEnum payPlatformEnum : PayPlatformEnum.values()) {
            if (bestPayTypeEnum.getPlatform().name().equals(payPlatformEnum.name())){
                return payPlatformEnum;
            }
        }
        throw new RuntimeException("错误的支付平台："+ bestPayTypeEnum.name());
    }
}
