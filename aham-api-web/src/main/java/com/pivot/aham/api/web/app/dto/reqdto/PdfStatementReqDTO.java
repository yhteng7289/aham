package com.pivot.aham.api.web.app.dto.reqdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class PdfStatementReqDTO extends BaseDTO {

    @JsonProperty("clientid")
    private String clientId;

    @JsonProperty("FileID")
    private String fileId;
}
