package com.pivot.aham.common.core.base;

import com.alibaba.dubbo.common.utils.StringUtils;
import com.sun.management.OperatingSystemMXBean;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.util.StringTokenizer;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by CPH on 2015/12/29.
 */
@Slf4j
public class SystemInfo {

    //获取内存使用率
    public static long getFreeMemery() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        return osmxb.getFreePhysicalMemorySize();
    }

    //获取总内存
    public static long getTotalMemery() {
        OperatingSystemMXBean osmxb = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        // 总的物理内存+虚拟内存
        return osmxb.getTotalPhysicalMemorySize();
    }

    //获取CPU使用
    public static double getCpuRate() {

        //操作系统
        String osName = System.getProperty("os.name");
        if (StringUtils.isEmpty(osName)) {
            return 9.99;
        }

        if (osName.toLowerCase().startsWith("windows")) {
            return 8.88;
        }

        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader brStat = null;
        StringTokenizer tokenStat;
        try {
            Process process = Runtime.getRuntime().exec("top -bn1");
            is = process.getInputStream();
            isr = new InputStreamReader(is);
            brStat = new BufferedReader(isr);
            brStat.readLine();
            brStat.readLine();
            String line = brStat.readLine();
            tokenStat = new StringTokenizer(line);
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            tokenStat.nextToken();
            String cpuUsage = tokenStat.nextToken();
            cpuUsage = cpuUsage.replace("%", "");
            Float usage = new Float(cpuUsage);
            return 1 - (float) usage / 100.0;
        } catch (Exception ioe) {
            freeResource(is, isr, brStat);
            log.error(ioe.getMessage(), ioe);
            return 7.77;
        } finally {
            freeResource(is, isr, brStat);
        }
    }

    private static void freeResource(InputStream is, InputStreamReader isr, BufferedReader br) {
        try {
            if (is != null) {
                is.close();
            }
            if (isr != null) {
                isr.close();
            }
            if (br != null) {
                br.close();
            }
        } catch (IOException ioe) {

        }
    }
}
