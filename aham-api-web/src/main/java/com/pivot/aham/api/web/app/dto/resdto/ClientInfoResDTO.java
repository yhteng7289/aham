package com.pivot.aham.api.web.app.dto.resdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class ClientInfoResDTO extends BaseDTO {

    @JsonProperty(value = "firstname")
    private String firstName;
    @JsonProperty(value = "lastname")
    private String lastName;
    @JsonProperty(value = "gender")
    private String gender;
    @JsonProperty(value = "citizenship")
    private String citizenShip;
    @JsonProperty(value = "nric")
    private String nric;
    @JsonProperty(value = "dob")
    private String dob;
    @JsonProperty(value = "singaporepr")
    private String singaporePr;
    @JsonProperty(value = "email")
    private String email;
    @JsonProperty(value = "homenumber")
    private String homeNumber;
    @JsonProperty(value = "mobilenumber")
    private String mobileNumber;
    @JsonProperty(value = "adressline1")
    private String adressline1;
    @JsonProperty(value = "adressline2")
    private String adressline2;
    @JsonProperty(value = "postalCode")
    private String postalCode;
    @JsonProperty(value = "clientid")
    private String clientId;

    private String resultCode;
    private String errorMsg;
}
