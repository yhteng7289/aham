/**
 *
 */
package com.pivot.aham.common.model;

import com.alibaba.fastjson.JSON;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月16日
 */
public class Login implements Serializable {

    @NotNull(message="登录名不能为空")
    @ApiModelProperty(value = "登录名", required = true)
    private String username;
    @NotNull(message="密码不能为空")
    @ApiModelProperty(value = "密码", required = true)
    private String password;
    @ApiModelProperty(value = "是否记住(默认false)", required = false)
    private Boolean rememberMe = false;

    public Login() {
    }

    public Login(String username, String password, Boolean rememberMe) {
        this.username = username;
        this.password = password;
        this.rememberMe = rememberMe;
    }

    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getRememberMe() {
        return rememberMe;
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public static void main(String[] args){
        Login login = new Login();
        login.setPassword("123456");
        login.setRememberMe(false);
        login.setUsername("admin");

        System.out.println(JSON.toJSONString(login));

    }
}
