package com.fousalert.restservice;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.fousalert.bean.Login;
import com.fousalert.bean.LoginResponse;
import com.fousalert.commonconstants.UtilityConstants;
import com.fousalert.database.utils.DataStoreConstants.ResultStatus;
import com.fousalert.utils.Constants;
import com.fousalert.utils.Context;

public class LoginTemplate {
	public Long loginAuthentication(Login login) {
		Long userId = null;
		try {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			HttpEntity<Login> requestEntity = new HttpEntity<Login>(login, requestHeaders);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			LoginResponse response = restTemplate.postForObject(UtilityConstants.LOGIN, requestEntity, LoginResponse.class);
			userId = response.getUserId();
			Context.getContext().put(Constants.SESSION_ID, response.getSessionId());
			Context.getContext().put(Constants.ENTITLEMENTS, response.getEntitlements());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return userId;
	}

	public boolean logout() {
		boolean status=false;
		try {
			HttpHeaders requestHeaders = new HttpHeaders();
			requestHeaders.setContentType(MediaType.APPLICATION_JSON);
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
			LoginResponse response = restTemplate.getForObject(UtilityConstants.LOGOUT, LoginResponse.class);
			System.out.println(response.toString());
			if (response.getStatus() == ResultStatus.SUCCESS) {
				status = true;
			} else{
				status = false;
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return status;
	}
}
