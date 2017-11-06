package com.baidu.ueditor.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.alibaba.fastjson.JSON;
import com.baidu.ueditor.ActionEnter;

@Configuration
@EnableConfigurationProperties(UeditorProperties.class)
@ConditionalOnClass(ActionEnter.class)
@ConditionalOnProperty(prefix="ueditor",value="enabled",matchIfMissing=true)
public class UeditorAutoConfiguration {

	@Autowired
	private UeditorProperties ueditorProperties;
	
	@Bean
	@ConditionalOnMissingBean(ActionEnter.class)
	public ActionEnter actionEnter(){
		return new ActionEnter(JSON.toJSONString(ueditorProperties));
		
	}
	

}
