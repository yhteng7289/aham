/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import java.util.List;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 *
 * @author HP
 */
@Data
@Accessors(chain = true)
public class UserStatementListResDTO extends BaseDTO {

    @JsonProperty(value = "statmentList")
    private List<UserStatementDetailsDTO> statmentList;

    private String resultCode;

    private String errorMsg;
}
