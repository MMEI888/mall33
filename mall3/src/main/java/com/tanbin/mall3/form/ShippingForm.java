package com.tanbin.mall3.form;

import lombok.Data;

/**
 * 入参表单
 */
@Data
public class ShippingForm {

    private String receiverName;

    private String receiverPhone;

    private String receiverMobile;

    private String receiverProvince;

    private String receiverCity;

    private String receiverDistrict;

    private String receiverAddress;

    private String receiverZip;
}
