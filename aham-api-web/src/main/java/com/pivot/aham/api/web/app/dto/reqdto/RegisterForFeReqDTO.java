package com.pivot.aham.api.web.app.dto.reqdto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class RegisterForFeReqDTO implements Serializable {

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
    private String riskprofile;
    private String portfolioid;
    private String salutation;
    private String firstname;
    private String lastname;
    private String citizenship;
    private String pr;
    private String nationalid;
    private String img1;
    private String img2;
    private String img3;
    private String dob;
    private String email;
    private String address1;
    private String address2;
    private String postalcode;
    private String country;
    private String mobileno;
    private String homeno;
    private String s1;
    private List<TaxForFeReqDTO> taxnoList;
    private String s2;
    private String s3;
    private String pname;
    private String prelation;
    private String pdesignaton;
    private String pcountry;
    private String cka1;
    private String cka2;
    private String cka3;
    private String gender;
}
