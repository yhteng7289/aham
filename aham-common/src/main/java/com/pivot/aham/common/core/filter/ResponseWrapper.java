package com.pivot.aham.common.core.filter;

import org.apache.commons.io.output.TeeOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;

/**
 * request包装类，满足重复获取renquestBody
 *
 * @author addison
 * @since 2018年11月28日
 */
public class ResponseWrapper extends HttpServletResponseWrapper {
    private Logger logger = LogManager.getLogger();

    private ByteArrayOutputStream byteArrayOutputStream;
    private PrintWriter pwrite;

    public ResponseWrapper(HttpServletResponse response) {
        super(response);
        byteArrayOutputStream = new ByteArrayOutputStream();
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        // 将数据写到 byte 中
        return new MyServletOutputStream();
    }

    /**
     * 重写父类的 getWriter() 方法，将响应数据缓存在 PrintWriter 中
     */
    @Override
    public PrintWriter getWriter() throws IOException {
        pwrite = new PrintWriter(new OutputStreamWriter(byteArrayOutputStream, "utf-8"));
        return pwrite;
    }

    public String getResponseData(String charset) throws IOException {
        byte[] bytes = getBytes();
        try {
            return new String(bytes, charset);
        } catch (UnsupportedEncodingException e) {
            return "";
        }

    }
    /**
     * 获取缓存在 byteArrayOutputStream 中的响应数据
     * @return
     */
    private byte[] getBytes() {
//        if(null != pwrite) {
//            pwrite.close();
//            return byteArrayOutputStream.toByteArray();
//        }
//
//        if(null != byteArrayOutputStream) {
//            try {
//                byteArrayOutputStream.flush();
//            } catch(IOException e) {
//                logger.error("获取缓存的response内容出错");
//            }
//        }
        return byteArrayOutputStream.toByteArray();
    }

    class MyServletOutputStream extends ServletOutputStream {
        private TeeOutputStream teeOutputStream = new TeeOutputStream(ResponseWrapper.super.getOutputStream(),byteArrayOutputStream);

        MyServletOutputStream() throws IOException {
        }

        @Override
        public void write(int b) throws IOException {
            // 将数据写到 stream　中
            teeOutputStream.write(b);
        }

        @Override
        public boolean isReady() {
            return false;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }

}
