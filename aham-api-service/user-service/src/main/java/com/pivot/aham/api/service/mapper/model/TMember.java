package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;


@TableName("t_member")
@Data
public class TMember extends BaseModel {

    /**
     * 姓名
     */
    @TableField("user_name")
    private String userName;
    /**
     * 密码
     */
    @TableField("password")
    private String password;
    /**
     * 电话
     */
    @TableField("phone")
    private String phone;

    /**
     * 昵称
     */
    @TableField("nick_name")
    private String nickName;




}
