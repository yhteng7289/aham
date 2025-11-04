package com.pivot.aham.api.service.mapper.model;

import com.pivot.aham.common.core.base.BaseModel;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class OpenAccountInfoQuestionPO extends BaseModel{
    private String lastName;
    private String firstName;
    private String mobileNum;
    private String firstQuestion;
    private String firstQuestionA;
    private String firstQuestionB;
    private String firstQuestionC;
    private String firstQuestionD;
    private String firstQuestionE;
    private String firstQuestionF;
    private String firstQuestionG;
    private String secondQuestion;
    private String thirdQuestion;
    private String thirdQuestionA;
    private String thirdQuestionB;
    private String thirdQuestionC;
    private String thirdQuestionD;
    private String fourthQuestion;
    private String fifthQuestion;
    private String sixthQuestion;
    private Integer isAgree;
    private Integer status;
}
