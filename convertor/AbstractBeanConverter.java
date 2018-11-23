package com.fousalert.convertor;

import com.google.gson.Gson;

public class AbstractBeanConverter {
	
	public static <T> T toBean(String jsonString, Class<T> clazz) {
		Gson gson = new Gson();
		return gson.fromJson(jsonString, clazz);
	}

}
