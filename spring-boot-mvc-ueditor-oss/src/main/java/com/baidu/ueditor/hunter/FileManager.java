package com.baidu.ueditor.hunter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.aliyun.oss.OSSClient;
import com.aliyun.oss.model.OSSObjectSummary;
import com.aliyun.oss.model.ObjectListing;
import com.baidu.ueditor.define.AppInfo;
import com.baidu.ueditor.define.BaseState;
import com.baidu.ueditor.define.MultiState;
import com.baidu.ueditor.define.State;

public class FileManager {
	
	private int count = 0;	
	public static String accessId;
	public static String accessKey;
	public static String downloadDNS;
	public static String bucketName;
	public static String uploadDirPrefix;
	public static String endpoint;
	
	 
	public FileManager ( Map<String, Object> conf ) {
		this.count = (Integer)conf.get( "count" );		
	}
	
	public State listFile ( int index,String marker ) {
		OSSClient client = null;
		State state = null;
		try {
			client = new OSSClient(endpoint, accessId, accessKey);
			ObjectListing objectListing =client.listObjects(bucketName, uploadDirPrefix);
			List<OSSObjectSummary> sums = objectListing.getObjectSummaries();
			List<String> fileList = new ArrayList<>();
			for (OSSObjectSummary fileInfo : sums) {
				fileList.add(fileInfo.getKey());
			}
			state = this.getState( fileList.toArray(new String[fileList.size()]) );
			state.putInfo( "start", index );
			state.putInfo( "isLast", objectListing.isTruncated()+"" );
			state.putInfo( "marker", objectListing.getMarker() );
			state.putInfo( "total", Integer.MAX_VALUE );
		} catch (Exception e) {
			state = new BaseState( false, AppInfo.NOT_EXIST );
		}										
		return state;
		
	}
	
	private State getState ( String[] files ) {		
		MultiState state = new MultiState( true );
		BaseState fileState = null;
				
		for ( String url : files ) {
			if ( url == null ) {
				break;
			}
			fileState = new BaseState( true );
			fileState.putInfo( "url", downloadDNS + "/" + url );
			state.addState( fileState );
		}		
		return state;		
	}		
}
