package com.pivot.aham.common.core.util;

import com.pivot.aham.common.core.exception.BusinessException;
import com.pivot.aham.common.core.exception.FtpException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPClientConfig;
import org.apache.commons.net.ftp.FTPReply;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
public class FTPUtil {

    private static final int RETRY_MAX_TIMES = 2;
    private static final int RETRY_SNAP_MILLISECONDS = 1000;
    private FTPClient ftp;
    private static final Object LOCK = new Object();

    public static void main(String[] args) {
        String server = "ftp.jimubox.com";
        String port = "21";
        String uname = "xuanji_portfolio_ro";
        String password = "p1JpeszKqHEr10U";
//        FTPUtil ftpUtil = new FTPUtil();
//        ftpUtil.login(server, port, uname, password);
//        List<String> lines = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
//
//        List<String> linessss = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");



        int count = 20;//并发线程数
        CyclicBarrier cyclicBarrier = new CyclicBarrier(count);
        ExecutorService executorService = Executors.newFixedThreadPool(count);
        int n = 0;

        for (int i = 0; i < count; i++) {
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        cyclicBarrier.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (BrokenBarrierException e) {
                        e.printStackTrace();
                    }
                    FTPUtil ftpUtil = new FTPUtil();
                    ftpUtil.login(server, port, uname, password);
                    List<String> lines = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
                    List<String> lines2 = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
                    List<String> lines1 = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
                    List<String> lines11 = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
                    List<String> lines1112 = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
                    List<String> lines111 = ftpUtil.readFileContent("/home/ftpuser/pivot/marketData/dailyClose.csv");
                    System.out.println(lines111.size());
                    ftpUtil.free();
                }
            });
        }
        executorService.shutdown();

    }


    public FTPUtil() {
        String server = PropertiesUtil.getString("ftp.jimubox.host");
        String port = PropertiesUtil.getString("ftp.jimubox.port");
        String uname = PropertiesUtil.getString("ftp.jimubox.user.name");
        String password = PropertiesUtil.getString("ftp.jimubox.user.password");
        if (!login(server, port, uname, password)) {
            throw new BusinessException("登陆ftp服务器失败");
        }
    }

    public List<String> readFileContent(String url) {
        return readFileContent(url, false);
    }

//    public List<String> readFileContentFromJimuboxFtp(String url, boolean holdTitle) {
//        return readFileContent(url, holdTitle);
//    }

    public List<String> readFileContent(String url, boolean holdTitle) {
        List<String> lines = null;
        InputStream in = null;
        try {
            in = retrieveFileStream(url);
            lines = IOUtils.readLines(in, "UTF-8");
            if (!holdTitle && CollectionUtils.isNotEmpty(lines)) {
                //删除表头
                lines.remove(0);
            }
        }
        catch (Exception e) {
            log.info("读取文件异常：", e);
            throw new BusinessException(e.getMessage(),e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                    ftp.completePendingCommand();
                } catch (IOException e) {
                    ErrorLogAndMailUtil.logError(log, e);
                }
            }
        }
        return lines;
    }

    /**
     * 从FTP服务器获取文件流
     *
     * @throws Exception
     */
    private InputStream retrieveFileStream(String url) throws Exception {
        InputStream inputStream = null;
        try {
            log.info("读取文件地址:{}", url);
            int count = 0;
            boolean isNeedTry = true;
            while (count < RETRY_MAX_TIMES && isNeedTry) {
                try {
                    log.info("读取文件[" + count + "]");
                    inputStream = ftp.retrieveFileStream(url);
                    isNeedTry = false;
                } catch (Exception ex) {
                    ErrorLogAndMailUtil.logError(log, ex);
                    count++;
                    if (count >= RETRY_MAX_TIMES) {
                        throw ex;
                    }
                    Thread.sleep(RETRY_SNAP_MILLISECONDS);
                }
            }
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
        }
        return inputStream;
    }


    /**
     * 初始化配置
     *
     * @throws IOException
     */
    private void initFTPClient() throws IOException {
        //文件类型,默认是ASCII
        ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftp.setControlEncoding("utf-8");
        //设置被动模式
        ftp.enterLocalPassiveMode();
        //设置数据超时
        ftp.setDataTimeout(20000);
        ftp.setBufferSize(1024);


        FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_NT);
        conf.setServerLanguageCode("zh");
        conf.setDefaultDateFormatStr("yyyy-MM-dd");
        ftp.configure(conf);
    }

    /**
     * 登录FTP
     */
    public boolean login(String server, String port, String uname, String password) {
        synchronized (LOCK) {
            if (ftp == null) {
                ftp = new FTPClient();
                ftp.setConnectTimeout(20000);
            }
            log.info(">>>>>>>>>>>>>>>>登录FTP");
            try {
                int count = 0;
                boolean isNeedTry = true;
                //连接重试
                while (count < RETRY_MAX_TIMES && isNeedTry) {
                    try {
                        log.info(">>>>>>>>>>>>>>>>连接尝试[" + count + "]");
                        ftp.connect(server, Integer.parseInt(port));
                        isNeedTry = false;
                    } catch (Exception ex) {
                        log.error(">>>>>>>>>>>>>>>FTP连接失败", ex);
                        ErrorLogAndMailUtil.logError(log, ex);
                        count++;
                        if (count >= RETRY_MAX_TIMES) {
                            throw ex;
                        }
                        Thread.sleep(RETRY_SNAP_MILLISECONDS);
                    }
                }
                //登陆
                if (FTPReply.isPositiveCompletion(ftp.getReplyCode())) {
                    try {
                        ftp.login(uname, password);
                    } catch (Exception e) {
                        throw new FtpException("FTP用户[" + uname + "]登陆失败!", e);
                    }
                } else {
                    throw new FtpException("FTP连接出错!");
                }
                //初始化设置
                initFTPClient();
                log.info(">>>>>>>>>>>>>>>>>登陆服务器成功!");
                return true;
            } catch (Exception e) {
                ErrorLogAndMailUtil.logError(log, e);
            }
        }
        return false;
    }

    /**
     * 释放FTP
     */
    public void free() {
        synchronized (LOCK) {
            if (ftp.isAvailable()) {
                try {
                    // 退出FTP
                    ftp.logout();
                } catch (IOException e) {
                    ErrorLogAndMailUtil.logError(log, e);
                }
            }
            if (ftp.isConnected()) {
                try {
                    // 断开连接
                    ftp.disconnect();
                } catch (IOException e) {
                    ErrorLogAndMailUtil.logError(log, e);
                }
            }
        }
    }


}
