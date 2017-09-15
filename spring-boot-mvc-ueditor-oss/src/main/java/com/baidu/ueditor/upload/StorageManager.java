package com.baidu.ueditor.upload;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;


import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectResult;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.FileType;
import com.baidu.ueditor.define.State;


public class StorageManager {
public static final int BUFFER_SIZE = 8192;
	
	public static String accessId;
	public static String accessKey;
	public static String downloadDNS;
	public static String bucketName;
	public static String uploadDirPrefix;
	public static String endpoint;
	private static final Logger logger = Logger.getLogger(StorageManager.class);
	
	public static State saveBinaryFile(byte[] data, String path) {
	
		
		State state = null ;
		String key = uploadDirPrefix + getFileName(path);
		String fileUrl = null;
		OSSClient client = null;
		try {
			client = new OSSClient(endpoint, accessId, accessKey);
			
			InputStream content = new ByteArrayInputStream(data); 
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(data.length);
			PutObjectResult  result= client.putObject(bucketName, key, content);
//			if(result.getETag())			
			fileUrl = "http://"+downloadDNS+"/"+key;
			state = new BaseState(true);
			state.putInfo("size", data.length);
			state.putInfo("title", path);
			state.putInfo("url",fileUrl);
		} catch (OSSException oe) {
			logger.error("Error Message: " + oe.getErrorCode()+"\n");
			logger.error("Error Code:       " + oe.getErrorCode()+"\n");
			logger.error("Request ID:      " + oe.getRequestId()+"\n");
			logger.error("Host ID:           " + oe.getHostId()+"\n");
			state = new BaseState(false, AppInfo.IO_ERROR);
		} finally{
			if(client != null){
				client.shutdown();
			}
			
		}
		return state;		
	}

	public static State saveFileByInputStream(InputStream is, String path,long maxSize) {
		State state = null;
		File tmpFile = getTmpFile();
		
		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);
			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();

			if (tmpFile.length() > maxSize) {
				tmpFile.delete();
				return new BaseState(false, AppInfo.MAX_SIZE);
			}

			state = saveTmpFile(tmpFile, path);
					
			if (!state.isSuccess()) {
				tmpFile.delete();
			}
			return state;
			
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}

	public static State saveFileByInputStream(InputStream is, String path) {
		State state = null;

		File tmpFile = getTmpFile();

		byte[] dataBuf = new byte[ 2048 ];
		BufferedInputStream bis = new BufferedInputStream(is, StorageManager.BUFFER_SIZE);

		try {
			BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream(tmpFile), StorageManager.BUFFER_SIZE);

			int count = 0;
			while ((count = bis.read(dataBuf)) != -1) {
				bos.write(dataBuf, 0, count);
			}
			bos.flush();
			bos.close();

			state = saveTmpFile(tmpFile, path);				
			
			if (!state.isSuccess()) {
				tmpFile.delete();
			}

			return state;
		} catch (IOException e) {
		}
		return new BaseState(false, AppInfo.IO_ERROR);
	}
	
	private static File getTmpFile() {
		File tmpDir = FileUtils.getTempDirectory();
		String tmpFileName = (Math.random() * 10000 + "").replace(".", "");
		return new File(tmpDir, tmpFileName);
	}

	private static State saveTmpFile(File tmpFile, String path) {
		State state = null ;
		String key = uploadDirPrefix + getFileName(path);
		String fileUrl = null;
		OSSClient client = null;
		try {
			client = new OSSClient(endpoint, accessId, accessKey);			
			InputStream content = new FileInputStream(tmpFile);
			ObjectMetadata meta = new ObjectMetadata();
			meta.setContentLength(tmpFile.length());
			client.putObject(bucketName, key, content);
			if(tmpFile.isFile() && tmpFile.exists()) {
				tmpFile.delete();
			}
			fileUrl = "http://"+downloadDNS+"/"+key;
			state = new BaseState(true);
			state.putInfo("size", tmpFile.length());
			state.putInfo("title", key);
			state.putInfo("url", fileUrl);
			
		} catch (OSSException oe) {
			logger.error("Error Message: " + oe.getErrorCode()+"\n");
			logger.error("Error Code:       " + oe.getErrorCode()+"\n");
			logger.error("Request ID:      " + oe.getRequestId()+"\n");
			logger.error("Host ID:           " + oe.getHostId()+"\n");
			state = new BaseState(false, AppInfo.IO_ERROR);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			state = new BaseState(false, AppInfo.IO_ERROR);
		} finally{
			if(client != null){
				client.shutdown();
			}
		}		
		return state;
	}
	
	private static String getFileName(String fileName) {
		String suffix =  FileType.getSuffixByFilename(fileName);
		return new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date())+ (int)(Math.random()*9000 +1000) + suffix;
	}
}
