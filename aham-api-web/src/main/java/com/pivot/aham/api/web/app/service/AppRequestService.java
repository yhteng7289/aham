package com.pivot.aham.api.web.app.service;

import com.pivot.aham.common.core.util.HttpResMsg;

/**
 * @author YYYz
 */
public interface AppRequestService {
    HttpResMsg callAppApi( String param, String appLoginUrl);
}
