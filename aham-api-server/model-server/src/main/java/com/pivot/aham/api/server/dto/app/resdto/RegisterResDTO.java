package com.pivot.aham.api.server.dto.app.resdto;

import com.pivot.aham.common.core.base.BaseDTO;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class RegisterResDTO extends BaseDTO {

    private String q1;
    private String q2;
    private String q3;
    private String q4;
    private String q5;
    private String q6;
    private String r1;
    private String r2;
    private String r3;
    private String r4;
    private String r5;
    private String r6;
    private String riskProfile;
    private String portfolioId;
    private String salutation;
    private String firstName;
    private String lastName;
    private String citizenship;
    private String pr;
    private String nationalId;
    private String img1;
    private String img2;
    private String img3;
    private String dob;
    private String email;
    private String address1;
    private String address2;
    private String postalCode;
    private String country;
    private String mobileNo;
    private String homeNo;
    private String s1;
    private List<TaxResDTO> taxNoList;
    private String s2;
    private String s3;
    private String pName;
    private String pRelation;
    private String pDesignation;
    private String pCountry;
    private String cka1;
    private String cka2;
    private String cka3;
}
