package com.fousalert.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Context {

	private static Context context = new Context();
	private static Map<Object, Object> contextMap = new ConcurrentHashMap<Object, Object>();
	private Context() {}
	
	public static Context getContext() {
		return context;
	}
	
	public void put(Object key, Object value) {
		contextMap.put(key, value);
	}
	
	public Object get(Object key) {
		return contextMap.get(key);
	}
	
	public void remove(Object key) {
		contextMap.remove(key);
	}
	
	public void removeByKeyValue(Object key, Object value) {
		Object targetObject = contextMap.get(key);
		if(targetObject instanceof List<?>) {
			List<?> list = (ArrayList)targetObject;
			list.remove(value);
		} else {
			contextMap.remove(key);
		}
		
	}
}
