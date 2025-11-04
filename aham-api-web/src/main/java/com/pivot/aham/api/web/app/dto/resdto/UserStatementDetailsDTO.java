/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 *
 * @author HP
 */
@Data
public class UserStatementDetailsDTO {

    @JsonProperty(value = "FileID")
    private String fileId;
    @JsonProperty(value = "ClientID")
    private String clientId;
    @JsonProperty(value = "FileName")
    private String fileName;
    @JsonProperty(value = "FileDownloadedDate")
    private String fileDownloadedDate;
}
