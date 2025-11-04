package com.pivot.aham.api.web.core;

import com.alibaba.csp.sentinel.slots.block.BlockException;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public final class ExceptionUtil {
    public static void handleException(BlockException ex) {
        log.error("Oops: " + ex.getClass().getCanonicalName(),ex);
    }
}
