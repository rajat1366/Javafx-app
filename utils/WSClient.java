package com.fousalert.utils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

public class WSClient {

	public <T, R> R sendRequest(String url, HttpMethod method, T dataToSend, HttpHeaders requestHeaders, Class<R> responseType) {
		
		HttpEntity<Object> requestEntity = new HttpEntity<Object>(dataToSend, requestHeaders);
		
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
		
		return restTemplate.exchange(url, method, requestEntity, responseType).getBody();
	}
	
	public <R> R sendGetRequest(String url, Class<R> responseType) {
		HttpHeaders requestHeaders = new HttpHeaders();
		requestHeaders.setContentType(MediaType.APPLICATION_JSON);
		return sendRequest(url, HttpMethod.GET, null, requestHeaders, responseType);
	}
	
}
