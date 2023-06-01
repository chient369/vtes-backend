package com.vtes.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/*
 * @Author : chien.tran van
 * @Date :   2023/05/26
 * Description: This class manages all keys used to access 3rd parties. 
 * */

@Component
public class RapidAPIAccessKeyManager {
	
	@Value("#{'${feign.client.api.key}'.split(',')}")
	private  List<String> accessKeys;

	private static Integer keyIndex = 0;

	public String getCurrentAccessKey() {		
		return accessKeys.get(keyIndex);
	}

	public void rotateAccesskey() {
	
		keyIndex = (keyIndex + 1) % accessKeys.size();
	}
}
