package com.pivot.aham.api.service.job.uob;

import com.pivot.aham.common.core.util.PropertiesUtil;

public class UobConstant {

    public static String getUobBalancePath() {
        return getFtp() + "/uob/";
    }

    public static String getFtp() {
        return PropertiesUtil.getString("ftp.pivot.uob.balance");
    }

}
