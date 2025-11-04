package com.pivot.aham.api.service.mapper.model;

import com.baomidou.mybatisplus.annotations.TableName;
import com.pivot.aham.common.core.base.BaseModel;
import com.pivot.aham.common.enums.NameAliasEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by WooiTatt
 *
 */
@Data
@Accessors(chain = true)
@TableName(value = "t_uob_name_alias",resultMap = "bankNameAliasMap")
public class BankNameAlias extends BaseModel {
    
    private String rechargeId;
    
    private String clientId;

    private String virtualAccountNo;
    
    private String sysClientName;
    
    private String bankClientName;
    
    private NameAliasEnum status;
    
    private String fileName1;
    
    private String fileName2;
    
    private Date createTime;
    
    private Date updateTime;
    
    private String reasonRejected;
    
    private MultipartFile file1;
    
    private MultipartFile file2;
           
     



}
