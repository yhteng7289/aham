package com.pivot.aham.common.core.filter;

import jodd.io.StreamUtil;
import org.springframework.util.StreamUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

/**
 * request包装类，满足重复获取renquestBody
 *
 * @author addison
 * @since 2018年11月28日
 */
public class RequestWrapper extends HttpServletRequestWrapper {
    
    private final byte[] body;  
    
    public RequestWrapper(HttpServletRequest request)throws IOException {
        super(request);
        //这样调用一下，requestwrapper才会有param，要不然没有，原因还不知道
        request.getParameterNames();
        body = StreamUtil.readBytes(request.getReader(), "UTF-8");
    }  
  
    @Override  
    public BufferedReader getReader() throws IOException {  
        return new BufferedReader(new InputStreamReader(getInputStream()));  
    }  
  
    @Override  
    public ServletInputStream getInputStream() throws IOException {  
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(body);
        return new ServletInputStream() {

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {

            }

            @Override
            public int read() throws IOException {  
                return byteArrayInputStream.read();
            }  
        };  
    }  
}