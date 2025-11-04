package com.pivot.aham.api.web.app.vo.req;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author YYYz
 */
@Data
@Accessors(chain = true)
public class SendFeedbackReqVo {
    private String firstName;
    private String lastName;
    private String mobileNumber;
    private String subject;
    private String message;
}
