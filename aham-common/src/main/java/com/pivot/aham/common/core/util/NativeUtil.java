package com.pivot.aham.common.core.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class NativeUtil {
	private static Logger logger = LogManager.getLogger();

	private NativeUtil() {
	}

	/** 获取机器名 */
	public static final String getHostName() {
		String hostName = "";
		try {
			hostName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			logger.error(e.getMessage(), e);
		}
		return hostName;
	}
}
