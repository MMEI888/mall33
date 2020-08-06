package com.tanbin.mall3.service;

import com.github.pagehelper.PageInfo;
import com.tanbin.mall3.Mall3ApplicationTests;
import com.tanbin.mall3.enums.ResponseEnum;
import com.tanbin.mall3.form.ShippingForm;
import com.tanbin.mall3.vo.ResponseVo;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import sun.rmi.runtime.Log;

import java.util.Map;

import static org.junit.Assert.*;
@Slf4j
public class IShippingServiceTest extends Mall3ApplicationTests {

    @Autowired
    private IShippingService shippingService;
    Integer uid = 14;
    @Test
    public void add() {
        ShippingForm form = new ShippingForm();
        form.setReceiverName("杰克");
        form.setReceiverPhone("7236480");
        form.setReceiverMobile("18069792102");
        form.setReceiverProvince("北京");
        form.setReceiverCity("北京市");
        form.setReceiverDistrict("海淀区");
        form.setReceiverAddress("颐景园");
        form.setReceiverZip("12345");
        ResponseVo<Map<String, Integer>> responseVo = shippingService.add(uid, form);
        log.info("responseVo={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void delete() {
        ResponseVo responseVo = shippingService.delete(uid, 7);
        log.info("responseVo={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void update() {
        ShippingForm form = new ShippingForm();
        form.setReceiverProvince("浙江省");
        ResponseVo responseVo = shippingService.update(1, 4, form);
        log.info("responseVo={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }

    @Test
    public void list() {
        ResponseVo<PageInfo> responseVo = shippingService.list(11, 1, 10);
        log.info("responseVo={}",responseVo);
        Assert.assertEquals(ResponseEnum.SUCCESS.getCode(),responseVo.getStatus());
    }
}