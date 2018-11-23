package com.fousalert.utils;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class DateUtil {
	
	public static String formatDate(Date date, String format) {
		String formattedDate = null;
		if(date != null) {
			DateFormat dateFormatter = new SimpleDateFormat(format);
			formattedDate = dateFormatter.format(date);
		}
		
		return formattedDate;
	}
	
	public static String formatDate(Date date, String format, TimeZone timezone) {
		String formattedDate = null;
		if(date != null) {
			DateFormat dateFormatter = new SimpleDateFormat(format);
			dateFormatter.setTimeZone(timezone);
			formattedDate = dateFormatter.format(date);
		}
		
		return formattedDate;
	}
	
	public static Date convertDateTimeZone(Date date, TimeZone targetTimeZone) {
		if(date == null) return null;
		
		TimeZone sourceTimeZone = TimeZone.getDefault();
		Calendar targetCalender = GregorianCalendar.getInstance(targetTimeZone);
		Calendar serverCalender = GregorianCalendar.getInstance(sourceTimeZone);
		
		int targetOffsetInMillis = targetTimeZone.getOffset(targetCalender.getTimeInMillis());
		int sourceOffsetInMillis = sourceTimeZone.getOffset(serverCalender.getTimeInMillis());
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MILLISECOND, (targetOffsetInMillis - sourceOffsetInMillis));

		return calendar.getTime();
	}
	
	public static Date modifyDate(Date date, int valueToAddOrSubtract, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(field, valueToAddOrSubtract);
		
		return calendar.getTime();
	}
	public static Date getModifiedStartDate(Date date, int valueToAddOrSubtract, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(field, valueToAddOrSubtract);
		
		return calendar.getTime();
	}
	
	public static Date getModifiedEndDate(Date date, int valueToAddOrSubtract, int field) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.SECOND, 59);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		calendar.set(Calendar.MINUTE, 59);
		calendar.set(Calendar.MILLISECOND, 999);
		calendar.add(field, valueToAddOrSubtract);
		
		return calendar.getTime();
	}
	
	public static String getConcatDateFields(Date date, boolean incrementMonth) {
		int monthIncr = incrementMonth ? 1 : 0;
		String finalDateString = null;
		if(date == null) return null;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		
		finalDateString = "" + calendar.get(Calendar.YEAR) + "" + leadZero((calendar.get(Calendar.MONTH) + monthIncr)) + "" +  leadZero(calendar.get(Calendar.DATE));
		finalDateString += "" +  leadZero(calendar.get(Calendar.HOUR_OF_DAY)) + "" +  leadZero(calendar.get(Calendar.MINUTE)) +  leadZero(calendar.get(Calendar.SECOND));
		
		return finalDateString;
	}
	
	public static String leadZero(int number) {
		String finalString = String.valueOf(number);
		finalString = (number < 10) ? "0" + finalString : finalString;
		
		return finalString;
	}
	
	public static Date getDate2yearsBack(Date date, int field, int yearsBefore) {
		Calendar cal = Calendar.getInstance();
		if(date != null) {
			cal.setTime(date);
		}
		
		cal.add(field, yearsBefore);
		
		return cal.getTime();
	}
	
	public static String getStringifiedStartDateWithTrailingZeros(Date date) {
		String finalStringDate = null;
		DecimalFormat decimalFormat= new DecimalFormat("00");
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		finalStringDate =  "" + calendar.get(Calendar.YEAR) + decimalFormat.format(Double.valueOf((calendar.get(Calendar.MONTH) + 1))) + decimalFormat.format(Double.valueOf((calendar.get(Calendar.DAY_OF_MONTH)))) + "000000";
		
		return finalStringDate;
	}
	
	public static Date convertToEST(Date date) {
		if(date == null) return null;
		return convertDateTimeZone(date, TimeZone.getTimeZone(Constants.TIMEZONE_EST_ID));
	}
	
	public static int getRemainingMinutesInCurrentDay() {
		Calendar now = Calendar.getInstance();
		Calendar dayEndCalendar = Calendar.getInstance();
		dayEndCalendar.set(Calendar.HOUR_OF_DAY, 23);
		dayEndCalendar.set(Calendar.MINUTE, 59);
		dayEndCalendar.set(Calendar.SECOND, 59);
		
		long timeRemaining = dayEndCalendar.getTime().getTime() - now.getTime().getTime();
		long minutesRemaining = (timeRemaining / 1000) / 60;
		return (int) minutesRemaining;
	}
	
	public static long getMillisFromMinutes(int minutes) {
		return minutes * 60 * 1000;
	}

}
