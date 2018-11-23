package com.fousalert.utils;

import java.text.DecimalFormat;

public class NumberUtil {
	
	public static double roundOf(double doubleValue, int places) {
		 if (places < 0) throw new IllegalArgumentException();
			long factor = (long) Math.pow(10, places);
			doubleValue = doubleValue * factor;
			long tmp = Math.round(doubleValue);
			return (double) tmp / factor;
	}
	
	public static double truncateDecimalPoints(double number, String decimalNumberFormat) {
		//System.out.println("number: " + number);
		DecimalFormat df = new DecimalFormat(decimalNumberFormat);
		String formattedDouble = df.format(number);
		return Double.valueOf(formattedDouble);
	}
	
	public static double truncateDecimalPointsTo2(double number) {
		return truncateDecimalPoints(number, Constants.DECIMAL_NUMBER_FORMAT_2);
	}
	
	

}
