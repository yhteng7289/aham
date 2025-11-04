package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 请填写类注释
 *
 * @author addison
 * @since 2018年11月29日
 */
@Data
@Accessors(chain = true)
public class MemberDTO extends BaseDTO{
    /**
     * 姓名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 电话
     */
    private String phone;
    /**
     * 昵称
     */
    private String nickName;

}
