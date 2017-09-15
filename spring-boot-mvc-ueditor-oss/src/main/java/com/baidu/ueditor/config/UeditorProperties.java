package com.baidu.ueditor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 百度ueditor配置
 * @author zrk
 * 修改成 阿里云OSS配置节点
 * @date 2016年11月10日 上午10:11:58
 */
@ConfigurationProperties(prefix = "ueditor")
public class UeditorProperties {
	
private String config = "{}";
	
	private String accessId="";
	
	private String accessKey="";
	
	private String endpoint = "";		

	private String bucketName="";

	private String downloadDNS="";
	
	private String uploadDirPrefix = "ueditor/file/";

	public String getConfig() {
		return config;
	}

	public void setConfig(String config) {
		this.config = config;
	}

	public String getAccessKey() {
		return accessKey;
	}

	public void setAccessKey(String accessKey) {
		this.accessKey = accessKey;
	}
	
	public String getUploadDirPrefix() {
		return uploadDirPrefix;
	}

	public void setUploadDirPrefix(String uploadDirPrefix) {
		this.uploadDirPrefix = uploadDirPrefix;
	}

	public String getAccessId() {
		return accessId;
	}

	public void setAccessId(String accessId) {
		this.accessId = accessId;
	}

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getBucketName() {
		return bucketName;
	}

	public void setBucketName(String bucketName) {
		this.bucketName = bucketName;
	}

	public String getDownloadDNS() {
		return downloadDNS;
	}

	public void setDownloadDNS(String downloadDNS) {
		this.downloadDNS = downloadDNS;
	}

}
