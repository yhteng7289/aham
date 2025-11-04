package com.pivot.aham.common.model;

import java.io.Serializable;

/**
 * 文件对象
 *
 * @author addison
 * @since 2018年11月16日
 */
@SuppressWarnings("serial")
public class FileInfo implements Serializable {
	/**
	 * OriginalFilename
	 */
	private String orgName;
	private String fileType;
	private String fileName;
	private Long fileSize;

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public String getFileType() {
		return fileType;
	}

	public void setFileType(String fileType) {
		this.fileType = fileType;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Long getFileSize() {
		return fileSize;
	}

	public void setFileSize(Long fileSize) {
		this.fileSize = fileSize;
	}
}
