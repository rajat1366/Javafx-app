package com.fousalert.utils;

import java.util.regex.Pattern;

public class ValidationUtil {

	public static boolean isNullOrEmpty(Object object, boolean isString) {
		boolean isNullOrEmpty = true;
		if(object != null) {
			if(isString) {
				String string = ((String)object);
				isNullOrEmpty = string.length() == 0;
			}
		}
		
		return isNullOrEmpty;
	}
	
	public static boolean isMatched(String string, String regExPattern) {
		boolean isMatched = false;
		if(string != null) {
			isMatched = string.matches(Pattern.compile(regExPattern).toString());
		}
		
		return isMatched;
	}
	
}
