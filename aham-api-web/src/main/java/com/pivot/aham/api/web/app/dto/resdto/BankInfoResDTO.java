package com.pivot.aham.api.web.app.dto.resdto;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class BankInfoResDTO extends BaseDTO{

    @JsonProperty(value = "userbankdetailvo")
    private List<UserBankDetailResDTO> userBankDetailVo;
    @JsonProperty(value = "clientid")
    private String clientId;

    private String resultCode;
    private String errorMsg;


    public static void main(String[] args) {
        String text = "{\"clientid\":\"3813\",\"userbankdetailViewModel\":[{\"bankname\":\"Ong Eng Huat\",\"bankacctno\":\"0185056576\"},{\"bankname\":\"Ong Eng Huat\",\"bankacctno\":\"01850565761\"},{\"bankname\":\"Ong Eng Huat\",\"bankacctno\":\"101888998778\"},{\"bankname\":\"Ong Eng Huat\",\"bankacctno\":\"2450000439\"},{\"bankname\":\"Ong Eng Huat\",\"bankacctno\":\"0185056576\"}]}";

        BankInfoResDTO bankInfoResDTO = JSON.parseObject(text, BankInfoResDTO.class);

        System.out.println(bankInfoResDTO);
    }
}
