package com.tanbin.mall3;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
@MapperScan(basePackages = "com.tanbin.mall3.dao")
@SpringBootApplication
public class Mall3Application {

    public static void main(String[] args) {
        SpringApplication.run(Mall3Application.class, args);
    }

}
