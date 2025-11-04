package com.pivot.aham.common.core.util;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.pivot.aham.common.core.exception.HttpClientException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPatch;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.config.ConnectionConfig;
import org.apache.http.config.ConnectionConfig.Builder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Desc:基于4.xx的httpclient的实现的工具类
 *
 * @author addison
 * @version 2017-07-11
 */
@Slf4j
public class HttpclientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger("saxo-response");

    private static HttpClientBuilder httpBuilder;
    /**
     * 字符集
     */
    public static final String CHARSET_UTF8 = "UTF-8";

    public static final String CHARSET_ISO_8859_1 = "ISO-8859-1";

    /**
     * 异常自动恢复处理, 使用HttpRequestRetryHandler接口实现请求的异常恢复
     */
    private static HttpRequestRetryHandler requestRetryHandler = (exception, executionCount, context) -> {
        // 设置恢复策略,在发生异常时候将自动重试3次
//        if(executionCount > 0) {
//            log.error("HTTP请求重发超过3次,不再重试！");
//            return false;
//        }
//            if(exception instanceof NoHttpResponseException) {//没有响应
//                LOGGER.error("HTTP请求第" + executionCount + "次,没有响应数据,将重试！",exception);
//                return true;
//            }
//        if(exception instanceof SSLHandshakeException) {//ssl连接失败
//            log.error("HTTP请求时SSL握手失败,不再是可用的连接,不再重试！",exception);
//            return false;
//        }
//        if (exception instanceof SSLException) {//ssl连接失败
//            log.error("HTTP请求时SSL握手失败,不再是可用的连接,不再重试！",exception);
//            return false;
//        }
//        if (exception instanceof InterruptedIOException) {//io操作中断
//            log.error("HTTP请求IO操作中断,不再重试！",exception);
//            return false;
//        }
//        if (exception instanceof UnknownHostException) {//未找到主机
//            log.error("HTTP未找到主机,不再重试！",exception);
//            return false;
//        }
//        if (exception instanceof ConnectTimeoutException) {//连接超时
//            log.error("HTTP请求第" + executionCount + "次,连接超时,将重试！",exception);
//            return true;
//        }
//
//        HttpClientContext clientContext = HttpClientContext.adapt(context);
//        HttpRequest request = clientContext.getRequest();
//        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest);
//        if (idempotent) {
//            // 如果请求被认为是幂等的，那么就重试。即重复执行不影响程序其他效果的
//            return true;
//        }
        return false;
    };

    /**
     * 使用ResponseHandler接口处理响应,HttpClient使用ResponseHandler会自动管理连接的释放,解决了对连接的释放管理
     */
//    private static ResponseHandler<String> responseHandler = response -> {
//        HttpEntity entity = response.getEntity();
//        if(entity != null) {
//            String charset = ContentType.get(entity).getCharset() == null ? CHARSET_UTF8 : ContentType.get(entity).getCharset().displayName();
//            return new String(EntityUtils.toByteArray(entity), charset);
//        } else {
//            return null;
    private static ResponseHandler<HttpResMsg> responseHandler = new ResponseHandler<HttpResMsg>() {
        // 自定义响应处理
        @Override
        public HttpResMsg handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
            HttpResMsg httpResMsg = new HttpResMsg();
            boolean isAttachement = false;
            Header[] headers = response.getAllHeaders();
            for (Header header : headers) {
                LOGGER.info("Key : " + header.getName() + " , Value : " + header.getValue());
                if (header.getName().toLowerCase().equalsIgnoreCase("content-disposition")
                        && header.getValue().toLowerCase().equalsIgnoreCase("attachment")) {
                    isAttachement = true;
                }
            }

            httpResMsg.setStatusCode(response.getStatusLine().getStatusCode());
            LOGGER.info("statusCode : " + response.getStatusLine().getStatusCode());
            HttpEntity entity = response.getEntity();
            String charset = "";
            if (entity != null && ContentType.get(entity) != null) {
                if (isAttachement) {
                    charset = CHARSET_ISO_8859_1;
                } else {
                    charset = ContentType.get(entity).getCharset() == null ? CHARSET_UTF8 : ContentType.get(entity).getCharset().displayName();
                }
                httpResMsg.setResponseStr(new String(EntityUtils.toByteArray(entity), charset));
            }
            LOGGER.info("charset : " + charset + " statusCode : " + response.getStatusLine().getStatusCode() + " httpResMsg : " + httpResMsg);
            return httpResMsg;
        }
    };

    /**
     *
     * Get方式提交,URL中包含查询参数, 格式：http://www.pintec.com?search=p&name=s.....
     *
     * @param url 提交地址
     * @return 响应消息
     * @throws IOException 发送请求失败时抛出异常
     */
    public static HttpResMsg get(String url) throws IOException {
        return get(url, Maps.newHashMap(), (Header[]) null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.pintec.com
     *
     * @param url 提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     * @throws IOException 发送请求失败时抛出异常
     */
    public static HttpResMsg get(String url, Map<String, String> params) throws IOException {
        return get(url, params, (Header[]) null);
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.pintec.com
     *
     * @param url 提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     * @throws IOException 发送请求失败时抛出异常
     */
    public static HttpResMsg get(String url, Map<String, String> params, Header... headers) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 将参数转为NameValuePair
        List<BasicNameValuePair> paramsList = getParamsList(params);
        if (paramsList != null && paramsList.size() > 0) {
            String formatParams = URLEncodedUtils.format(paramsList, CHARSET_UTF8);
            url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url.substring(0, url.indexOf("?") + 1) + formatParams);
        }
        HttpClientBuilder httpClientBuilder = getHttpBuilder(CHARSET_UTF8);
        CloseableHttpClient httpclient = httpClientBuilder.build();
        HttpGet hg = new HttpGet(url);
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    hg.addHeader(header);
                }
            }
//            hg.setConfig(getRequestConfig());
            responseStr = httpclient.execute(hg, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-get请求异常,url=" + url, e);
        }
        return responseStr;
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.pintec.com
     *
     * @param url 提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     * @throws IOException 发送请求失败时抛出异常
     */
    public static HttpResMsg uobGetBalance() {
        HttpResMsg responseStr = null;
        try {
            HttpClientBuilder httpClientBuilder = getHttpBuilder(CHARSET_UTF8);
            CloseableHttpClient httpclient = httpClientBuilder.build();
            HttpGet hg = new HttpGet("https://api-uat.uob.com.sg/business/v1/accounts/balance");
            // 发送请求,得到响应

            hg.setHeader(new BasicHeader("Country", "SG"));
            hg.setHeader(new BasicHeader("Application-ID", "9ad14a72-d457-4dc7-a2a8-8e96eb1a0b2c"));
            hg.setHeader(new BasicHeader("API-Key", "7693f42b-215f-4def-be00-d311766d43b0"));
            hg.setHeader(new BasicHeader("Client-ID", "678960ac-32ff-4d5b-b30a-2d4755191bb0"));
            hg.setHeader(new BasicHeader("Authorization", "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiJ9.eyJhY2NvdW50cyI6eyJhY2NvdW50SW5mb3JtYXRpb24iOlt7ImFjY291bnROdW1iZXIiOiIzNTIzMDk1NzM5IiwiYWNjb3VudEN1cnJlbmN5IjoiU0dEIiwiYWNjb3VudFR5cGUiOiJEIn1dLCJ1ZW4iOiIyMDE3MTYxNTBEIn19.bbIGPHl0ypFSGm5W0bYiplj1BZ8PysXt398w_41Hk1-T0ijA44tWihD-IX5ddI1qIgNqn2Ey3fGQEE-t5RJ09ehs19giX0TxtgbF48CUPjW1cTBYNhQdcnYXQtRAU_IfLH7JEEOZthq5rkoCTVhYJHAe_P-skaXx5y4kqcDTs-az_GlKGVilN6oDRnGShs23Kt9wXupOMA9aVYlBOc0CuS3lGoU7HZdgRewfKFgyEXrqFHzbmR8yT1U93UKHVJqta80_j4pB7XRXMmHA7djlOhQpz3HXbF_Wf8jl9FK-r0-FGc6iAJ4t1x5hQABFccH4D1R5CtTiUEQFrGs1nAbsxfB3gp_c7nkY-RNr_HiwOZAQk23_2uqQeT1YmNbQ0h2ox7lzcXfcyeb6Qg6B76-ABwqt7NpcDcCWUWek3Unz3TqVMt7FY3G-6cXHpDBR0BpP-l-95GEkoNVI4PzswWQu-aMTh6BLCFx_FVkrgT82nAouSas73XqpjhCdRfNIh0qKTdo5m2SbaIafmnkPKi6UttOLaslDLdDJGRlo1yKoZnBU5ln7HppZAVUiPHfiXIGRbPfcbzc0BHPMrDlCf3tj6uYRegXSNORIfxrdLbTD1OILBh_OckbEiIUHRtxPdR2nq6iMO61ayAncTUDciuWYHFJAc8RZLBaAhs2qcFFlAmI"));

            Builder connectionConfigBuilder = ConnectionConfig.custom();
            connectionConfigBuilder.setCharset(Charset.forName("UTF-8"));
            httpClientBuilder.setDefaultConnectionConfig(connectionConfigBuilder.build());
            httpClientBuilder.setSSLSocketFactory(createSSLConnSocketFactory());
            httpClientBuilder.setDefaultRequestConfig(getRequestConfig());
            log.info("httpClientBuilder : " + httpClientBuilder);
            TrustStrategy acceptingTrustStrategy = (cert, authType) -> true;
            SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
            SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext,
                    NoopHostnameVerifier.INSTANCE);

            Registry<ConnectionSocketFactory> socketFactoryRegistry
                    = RegistryBuilder.<ConnectionSocketFactory>create()
                            .register("https", sslsf)
                            .register("http", new PlainConnectionSocketFactory())
                            .build();
            log.info("socketFactoryRegistry : " + socketFactoryRegistry);
            BasicHttpClientConnectionManager connectionManager
                    = new BasicHttpClientConnectionManager(socketFactoryRegistry);
            CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslsf)
                    .setConnectionManager(connectionManager).build();
            log.info("httpClient : " + httpClient);
            HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
            ResponseEntity<String> response = new RestTemplate(requestFactory).exchange("https://api-uat.uob.com.sg/business/v1/accounts/balance", HttpMethod.GET, null, String.class);
            log.info("response : " + response);
            responseStr = httpclient.execute(hg, responseHandler);
        } catch (Exception e) {
            e.printStackTrace();
//            ErrorLogAndMailUtil.logError(log, e);
//            throw new HttpClientException("http-get请求异常,url=" + "", e);
        }

        return responseStr;
    }

    /**
     * Get方式提交,URL中不包含查询参数, 格式：http://www.pintec.com
     *
     * @param url 提交地址
     * @param params 查询参数集, 键/值对
     * @return 响应消息
     * @throws IOException 发送请求失败时抛出异常
     */
    public static HttpResMsg delete(String url, Map<String, String> params, Header... headers) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 将参数转为NameValuePair
        List<BasicNameValuePair> paramsList = getParamsList(params);
        if (paramsList != null && paramsList.size() > 0) {
            String formatParams = URLEncodedUtils.format(paramsList, CHARSET_UTF8);
            url = (url.indexOf("?")) < 0 ? (url + "?" + formatParams) : (url.substring(0, url.indexOf("?") + 1) + formatParams);
        }
        HttpClientBuilder httpBuilder = getHttpBuilder(CHARSET_UTF8);
        CloseableHttpClient httpclient = httpBuilder.build();
        HttpDelete hd = new HttpDelete(url);
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    hd.addHeader(header);
                }
            }
//            hg.setConfig(getRequestConfig());
            responseStr = httpclient.execute(hd, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-get请求异常,url=" + url, e);
        }
        return responseStr;
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://www.pintec.com
     *
     * @param url 提交地址
     * @param params 提交参数集, 键/值对
     * @return 响应消息
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg post(String url, Map<String, String> params) throws IOException {
        return post(url, params, null, (Header[]) null);
    }

    /**
     * Post方式提交
     *
     * @param url 提交地址
     * @param params 提交参数集, 键/值对
     * @param headers 请求Header
     * @return post请求结果
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg post(String url, Map<String, String> params, Header... headers) throws IOException {
        return post(url, params, null, headers);
    }

    /**
     * Post方式提交
     *
     * @param url 提交地址
     * @param params 提交参数
     * @param headers 请求Header
     * @return post请求结果
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg post(String url, String params, Header... headers) throws IOException {
        return post(url, params, null, headers);
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://wwww.10101111.com
     *
     * @param url 提交地址
     * @param params 提交参数集, 键/值对
     * @param charset 参数提交编码集
     * @param headers 请求Header
     * @return 响应消息
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg post(String url, Map<String, String> params, String charset, Header... headers) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 创建HttpClient实例
        HttpClientBuilder httpBuilder = getHttpBuilder(charset);
        CloseableHttpClient httpclient = httpBuilder.build();
        UrlEncodedFormEntity formEntity;
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {

            if (StringUtils.isEmpty(charset)) {
                formEntity = new UrlEncodedFormEntity(getParamsList(params));
            } else {
                formEntity = new UrlEncodedFormEntity(getParamsList(params), charset);
            }

            formEntity.setContentType("application/x-www-form-urlencoded");

            HttpPost hp = new HttpPost(url);
            hp.setEntity(formEntity);
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    hp.addHeader(header);
                }
            }
//            hp.setConfig(getRequestConfig());
            responseStr = httpclient.execute(hp, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-post请求异常,url=" + url, e);
        }
        return responseStr;
    }

    /**
     * Patch方式提交,URL中不包含提交参数, 格式：http://wwww.10101111.com
     *
     * @param url 提交地址
     * @param params 提交参数集, 键/值对
     * @param charset 参数提交编码集
     * @param headers 请求Header
     * @return 响应消息
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg patch(String url, Map<String, String> params, String charset, Header... headers) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 创建HttpClient实例
        HttpClientBuilder httpBuilder = getHttpBuilder(charset);
        CloseableHttpClient httpclient = httpBuilder.build();
        UrlEncodedFormEntity formEntity;
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {
            if (StringUtils.isEmpty(charset)) {
                formEntity = new UrlEncodedFormEntity(getParamsList(params));
            } else {
                formEntity = new UrlEncodedFormEntity(getParamsList(params), charset);
            }

            HttpPatch hp = new HttpPatch(url);
            hp.setEntity(formEntity);
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    hp.addHeader(header);
                }
            }
//            hp.setConfig(getRequestConfig());
            responseStr = httpclient.execute(hp, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-post请求异常,url=" + url, e);
        }
        return responseStr;
    }

    /**
     * Patch方式提交,URL中不包含提交参数, 格式：http://wwww.10101111.com
     *
     * @param url 提交地址
     * @param params 提交参数集, 键/值对
     * @param charset 参数提交编码集
     * @param headers 请求Header
     * @return post请求结果
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg patch(String url, String params, String charset, Header... headers) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 创建HttpClient实例
        HttpClientBuilder httpBuilder = getHttpBuilder(charset);
        CloseableHttpClient httpclient = httpBuilder.build();
        StringEntity formEntity;
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {
            formEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
            HttpPatch hp = new HttpPatch(url);
            hp.setEntity(formEntity);
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    hp.addHeader(header);
                }
            }
//            hp.setConfig(getRequestConfig());
            responseStr = httpclient.execute(hp, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-post请求异常,url=" + url, e);
        }
        return responseStr;
    }

    /**
     * Post方式提交,URL中不包含提交参数, 格式：http://wwww.10101111.com
     *
     * @param url 提交地址
     * @param params 提交参数集, 键/值对
     * @param charset 参数提交编码集
     * @param headers 请求Header
     * @return post请求结果
     * @throws IOException 请求失败时抛出异常
     */
    public static HttpResMsg post(String url, String params, String charset, Header... headers) throws IOException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 创建HttpClient实例
        HttpClientBuilder httpBuilder = getHttpBuilder(charset);
        CloseableHttpClient httpclient = httpBuilder.build();
        StringEntity formEntity;
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {
            formEntity = new StringEntity(params, ContentType.APPLICATION_JSON);
            HttpPost hp = new HttpPost(url);
            hp.setEntity(formEntity);
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    hp.addHeader(header);
                }
            }
//            hp.setConfig(getRequestConfig());
            responseStr = httpclient.execute(hp, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-post请求异常,url=" + url, e);
        }
        return responseStr;
    }

    public static HttpResMsg options(String url, Header... headers) throws NoSuchAlgorithmException, KeyStoreException {
        if (StringUtils.isEmpty(url)) {
            return null;
        }
        // 创建HttpClient实例
        HttpClientBuilder httpBuilder = getUobHttpBuilder(CHARSET_UTF8);
        CloseableHttpClient httpclient = httpBuilder.build();
        StringEntity formEntity;
        // 发送请求,得到响应
        HttpResMsg responseStr;
        try {
            HttpOptions ho = new HttpOptions(url);
            // headers存在时,设置header
            if (headers != null) {
                for (Header header : headers) {
                    ho.addHeader(header);
                }
            }
//            hp.setConfig(getRequestConfig());
            responseStr = httpclient.execute(ho, responseHandler);
        } catch (Exception e) {
            ErrorLogAndMailUtil.logError(log, e);
            throw new HttpClientException("http-post请求异常,url=" + url, e);
        }
        return responseStr;
    }

    /**
     * 获取DefaultHttpClient实例
     *
     * @param charset 参数编码集, 可空,为空时使用UTF-8编码
     * @return DefaultHttpClient 对象
     */
    public static HttpClientBuilder getHttpBuilder(String charset) {
        if (httpBuilder == null) {
            synchronized (HttpclientUtils.class) {
                if (httpBuilder == null) {
                    httpBuilder = HttpClients.custom();
                }
            }
            // 设置连接池大小
            httpBuilder.setMaxConnTotal(150);
            httpBuilder.setMaxConnPerRoute(100);

            // 设置重试处理器
            httpBuilder.setRetryHandler(requestRetryHandler);

            // 如果没有指定编码集,默认使用UTF-8编码
            if (StringUtils.isBlank(charset)) {
                charset = CHARSET_UTF8;
            }

            Builder connectionConfigBuilder = ConnectionConfig.custom();
            connectionConfigBuilder.setCharset(Charset.forName(charset));
            httpBuilder.setDefaultConnectionConfig(connectionConfigBuilder.build());
            httpBuilder.setSSLSocketFactory(createSSLConnSocketFactory());
            httpBuilder.setDefaultRequestConfig(getRequestConfig());
        }
        return httpBuilder;
    }

    /**
     * 获取DefaultHttpClient实例
     *
     * @param charset 参数编码集, 可空,为空时使用UTF-8编码
     * @return DefaultHttpClient 对象
     */
    public static HttpClientBuilder getUobHttpBuilder(String charset) throws NoSuchAlgorithmException, KeyStoreException {
        if (httpBuilder == null) {
            synchronized (HttpclientUtils.class) {
                if (httpBuilder == null) {
                    httpBuilder = HttpClients.custom();
                }
            }
            // 设置连接池大小
            httpBuilder.setMaxConnTotal(150);
            httpBuilder.setMaxConnPerRoute(100);

            // 设置重试处理器
            httpBuilder.setRetryHandler(requestRetryHandler);

            // 如果没有指定编码集,默认使用UTF-8编码
            if (StringUtils.isBlank(charset)) {
                charset = CHARSET_UTF8;
            }
        }
        return httpBuilder;
    }

    /**
     * 创建ssl连接工厂
     *
     * @return
     */
    private static SSLConnectionSocketFactory createSSLConnSocketFactory() {
        SSLConnectionSocketFactory sslsf = null;
        try {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (chain, authType) -> true).build();
            sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }
        return sslsf;
    }

    /**
     * <p>
     * 设置请求超时时间</p>
     * <p>
     * connectionRequestTimeout=1000</p>
     * <p>
     * connectTimeout=5000</p>
     * <p>
     * socketTimeout=5000</p>
     *
     * @return RequestConfig
     */
    public static RequestConfig getRequestConfig() {
        // 设置从connection manager获取connection的超时时间
        return RequestConfig.custom().setConnectionRequestTimeout(15000)
                // 设置连接超时时间
                .setConnectTimeout(30000)
                // 设置Socket超时时间
                .setSocketTimeout(30000).build();
        
        // return RequestConfig.custom().setConnectionRequestTimeout(5000)
                // 设置连接超时时间
         //       .setConnectTimeout(20000)
                // 设置Socket超时时间
           //     .setSocketTimeout(20000).build();
    }

    /**
     * 将传入的键/值对参数转换为NameValuePair参数集
     *
     * @param paramsMap 参数集, 键/值对
     * @return NameValuePair参数集
     */
    public static List<BasicNameValuePair> getParamsList(Map<String, String> paramsMap) {
        if (paramsMap == null || paramsMap.size() == 0) {
            return null;
        }
        List<BasicNameValuePair> params = new ArrayList<BasicNameValuePair>();
        Set<Entry<String, String>> s = paramsMap.entrySet();
        for (Entry map : s) {
            String k = (String) map.getKey();
            String v = (String) map.getValue();
            params.add(new BasicNameValuePair(k, v));
        }
        return params;
    }

    public static void main(String[] args) throws IOException {
        System.out.println(JSON.toJSONString(HttpclientUtils.patch("https://gateway.saxobank.com/sim/openapi/port/v1/accounts/me", "123", "")));
    }
}
