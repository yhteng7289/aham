package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.NameAliasEnum;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * Created by WooiTatt
 */
@Data
@Accessors(chain = true)
public class BankNameAliasResDTO extends BaseDTO {
    
    private String rechargeId;
    
    private String clientId;
    
    private String sysClientName;
    
    private String bankClientName;
    
    private String virtualAccountNo;
    
    private NameAliasEnum status;
    
    private String file1;
    
    private String file2;
    
    private Integer pageNo;
    
    private Integer pageSize;
    
    
}
