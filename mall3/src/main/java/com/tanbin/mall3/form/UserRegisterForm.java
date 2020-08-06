package com.tanbin.mall3.form;

import lombok.Data;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Data
public class UserRegisterForm {
    //字段不能为空格
    @NotBlank
    private String username;
    @NotBlank
    private String password;
    @NotBlank
    private String email;
}
