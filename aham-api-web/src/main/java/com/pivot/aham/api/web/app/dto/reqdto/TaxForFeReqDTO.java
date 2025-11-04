package com.pivot.aham.api.web.app.dto.reqdto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class TaxForFeReqDTO implements Serializable {

    @JsonProperty(value = "Taxno")
    private String Taxno;
    @JsonProperty(value = "Country")
    private String Country;

    @JsonProperty(value = "Taxno")
    public String getTaxno() {
        return Taxno;
    }

    @JsonProperty(value = "Taxno")
    public void setTaxno(String taxno) {
        Taxno = taxno;
    }

    @JsonProperty(value = "Country")
    public String getCountry() {
        return Country;
    }

    @JsonProperty(value = "Country")
    public void setCountry(String country) {
        Country = country;
    }
}
