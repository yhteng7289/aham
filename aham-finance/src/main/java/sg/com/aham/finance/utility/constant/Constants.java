/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sg.com.aham.finance.utility.constant;

import java.math.BigDecimal;

/**
 * 常量表
 *
 * @author addison
 * @version 1.0
 */
public interface Constants {

    String PFT_UPDATE_ASSET = "PFT_UPDATE_ASSET";

    /**
     * downloadPath
     */
    String TMP_DOWNLOAD_PATH = System.getProperty("java.io.tmpdir");

    /**
     * 自建基金的初始化净值
     */
    BigDecimal INIT_FUND_NAV = new BigDecimal("1");
    /**
     * mock的收市价
     */
    BigDecimal CLOSE_PRICE = new BigDecimal("10");
    /**
     * unBuy的产品编号
     */
    String UN_BUY_PRODUCT_CODE = "unBuyCash";
    /**
     * cash的产品编号
     */
    String CASH = "cash";
    /**
     * Main的cash的产品编号
     */
    String MAIN_CASH = "CASH";
    /**
     * Sub的cash的产品编号
     */
    String SUB_CASH = "Cash1";
    /**
     * 账户投资金额分界
     */
    String INVEST_TYPE = "10000";
    /**
     * 购买etf分界
     */
    String BUY_ETF_LIMIT = "3000";
    /**
     * swagger路径前缀
     */
    String SWAGGER_URL_PREFIX = "/swagger";
    /**
     * token关键字
     */
    String TOKEN_NAME = "token";
    /**
     * 异常信息统一头信息
     */
    String Exception_Head = "非常遗憾的通知您,程序发生了异常:";

    /**
     * 操作名称
     */
    String OPERATION_NAME = "OPERATION_NAME";
    /**
     * 客户端语言
     */
    String USERLANGUAGE = "userLanguage";
    /**
     * 客户端主题
     */
    String WEBTHEME = "webTheme";
    /**
     * 当前用户
     */
    String CURRENT_USER = "CURRENT_USER";
    /**
     * 客户端信息
     */
    String USER_AGENT = "USER-AGENT";
    /**
     * 客户端信息
     */
    String USER_IP = "USER_IP";
    /**
     * 登录地址
     */
    String LOGIN_URL = "/login.html";
    /**
     * 缓存命名空间
     */
    String CACHE_NAMESPACE = "Aham:";
    /**
     * 缓存命名空间
     */
    String SYSTEM_CACHE_NAMESPACE = "S:Aham:";
    /**
     * 缓存命名空间
     */
    String CACHE_NAMESPACE_LOCK = "L:Aham:";
    /**
     * 上次请求地址
     */
    String PREREQUEST = CACHE_NAMESPACE + "PREREQUEST";
    /**
     * 上次请求时间
     */
    String PREREQUEST_TIME = CACHE_NAMESPACE + "PREREQUEST_TIME";
    /**
     * 非法请求次数
     */
    String MALICIOUS_REQUEST_TIMES = CACHE_NAMESPACE + "MALICIOUS_REQUEST_TIMES";
    /**
     * 在线用户数量
     */
    String ALLUSER_NUMBER = SYSTEM_CACHE_NAMESPACE + "ALLUSER_NUMBER";
    /**
     * TOKEN
     */
    String TOKEN_KEY = SYSTEM_CACHE_NAMESPACE + "TOKEN_KEY:";
    /**
     * shiro cache
     */
    String REDIS_SHIRO_CACHE = SYSTEM_CACHE_NAMESPACE + "SHIRO-CACHE:";
    /**
     * SESSION
     */
    String REDIS_SHIRO_SESSION = SYSTEM_CACHE_NAMESPACE + "SHIRO-SESSION:";
    /**
     * 默认数据库密码加密key
     */
    String DB_KEY = "95270000";
    /**
     * 临时目录
     */
    String TEMP_DIR = "/temp/";
    /**
     * 请求报文体
     */
    String REQUEST_BODY = "Aham.requestBody";
    /**
     * Ftp 文件根地址
     */
    String FTP_BASE_FOLDER = "/home/ftpuser/pivot";

    BigDecimal GST_RATE_PRICE = new BigDecimal("0.07");

    /**
     * 日志表状态
     */
    interface JOBSTATE {

        /**
         * 日志表状态，初始状态，插入
         */
        String INIT_STATS = "I";
        /**
         * 日志表状态，成功
         */
        String SUCCESS_STATS = "S";
        /**
         * 日志表状态，失败
         */
        String ERROR_STATS = "E";
        /**
         * 日志表状态，未执行
         */
        String UN_STATS = "N";
    }

    /**
     * 短信验证码类型
     */
    interface MSGCHKTYPE {

        /**
         * 注册
         */
        String REGISTER = CACHE_NAMESPACE + "REGISTER:";
        /**
         * 登录
         */
        String LOGIN = CACHE_NAMESPACE + "LOGIN:";
        /**
         * 修改密码验证码
         */
        String CHGPWD = CACHE_NAMESPACE + "CHGPWD:";
    }

    interface TIMES {

        long SECOND = 1000; // 1秒 java已毫秒为单位
        long MINUTE = SECOND * 60; // 一分钟
        long HOUR = MINUTE * 60; // 一小时
        long DAY = HOUR * 24; // 一天
        long WEEK = DAY * 7; // 一周
        long YEAR = DAY * 365; // 一年
    }
}
