package com.pivot.aham.api.server.dto;

import com.pivot.aham.common.core.base.BaseDTO;
import com.pivot.aham.common.enums.NameAliasEnum;
import lombok.Data;
import lombok.experimental.Accessors;
import org.springframework.web.multipart.MultipartFile;


/**
 * Created by WooiTatt
 */
@Data
@Accessors(chain = true)
public class BankNameAliasReqDTO extends BaseDTO {
    
    private String rechargeId;
    
    private String clientId;
    
    private String sysClientName;
    
    private String bankClientName;
    
    private String virtualAccountNo;
    
    private NameAliasEnum status;
    
    private String fileName1;
    
    private String fileName2;
    
    private Integer pageNo;
    
    private Integer pageSize;
    
    private MultipartFile file1;
            
    private MultipartFile file2;
    
    private String reasonRejection;
    
}
