package com.pivot.aham.common.core.support.file.ftp;

/**
 * @author renshuaishuai on 16/12/26.
 */
public final class FTPManagement {
    private FTPManagement() {
    }

    private static final ThreadLocal<FTPClientUtil> THREAD_LOCAL = new ThreadLocal<>();

    public static void open() throws Exception {
        final FTPClientUtil util = THREAD_LOCAL.get();
        if (util != null) {
            return;
        }
        THREAD_LOCAL.set(new FTPClientUtil());
    }

    /**
     * 获取FTPClientUtil链接
     * 调用该方法时需要在最近的public方法处加上@FTP的注解
     * 通过{@link FTPAspect} 获取链接
     *
     * @return
     */
    public static FTPClientUtil get() {
        return THREAD_LOCAL.get();
    }

    public static void close() {
        THREAD_LOCAL.remove();
    }
}
