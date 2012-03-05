package de.beuth.sp.belegsystem.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import org.apache.commons.lang.NullArgumentException;

public class CalendarUtil {
	
	/**
	 * Nur zum testen.
	 * @param args
	 */
	public static void main(final String[] args) {
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.YEAR, Locale.getDefault()));
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.MONTH, Locale.getDefault()));
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.DAY, Locale.getDefault()));
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.HOUR, Locale.getDefault()));
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.MINUTE, Locale.getDefault()));
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.SECOND, Locale.getDefault()));
		System.out.println(getCalendarDateTime(Calendar.getInstance(), TimeDetail.MILLISECOND, Locale.getDefault()));
	}
	
	public enum TimeDetail {
		MILLISECOND, SECOND, MINUTE, HOUR, DAY, MONTH, YEAR     
	}
	
	public static String getCalendarDateTime(final Calendar cal, TimeDetail tillTimeDetail, Locale locale) {
		if (cal == null) throw new NullArgumentException("cal");
		if (tillTimeDetail == null) tillTimeDetail = TimeDetail.MINUTE;
		if (locale == null) locale = Locale.getDefault();
		
		switch (tillTimeDetail) {
			case MILLISECOND:
				return getFormattedDate(cal, "HH:mm:ss:SSS dd:MM:YYYY", locale);
			case SECOND:
				return getFormattedDate(cal, "HH:mm:ss dd:MM:YYYY", locale);
			case MINUTE:
				return getFormattedDate(cal, "HH:mm dd:MM:YYYY", locale);
			case HOUR:
				return getFormattedDate(cal, "HH'h' dd:MM:YYYY", locale);
			case DAY:
				return getFormattedDate(cal, "dd:MM:YYYY", locale);
			case MONTH:
				return getFormattedDate(cal, "MM/YYYY", locale);
			case YEAR:
				return getFormattedDate(cal, "YYYY", locale);
			default:
				break;
		}
		return "";
	}
	
	
	public static String getFormattedDate(final Calendar cal, final String pattern, Locale locale) {
		if (locale==null) locale = Locale.getDefault();		
		final SimpleDateFormat dateFormat = new SimpleDateFormat(pattern, locale);
		return dateFormat.format(cal.getTime());
	}
	
	public static Calendar setToZeroDetailAndBelow(Calendar cal, TimeDetail detailAndBelow) {
		switch (detailAndBelow) {
			case YEAR:
				cal.set(Calendar.YEAR, 0);
			case MONTH:
				cal.set(Calendar.MONTH, 0);
			case DAY:
				cal.set(Calendar.DAY_OF_MONTH, 0);
			case HOUR:
				cal.set(Calendar.HOUR_OF_DAY, 0);
			case MINUTE:
				cal.set(Calendar.MINUTE, 0);
			case SECOND:
				cal.set(Calendar.SECOND, 0);
			case MILLISECOND:
				cal.set(Calendar.MILLISECOND, 0);			
			default:
				break;			
		}
		return cal;
	}


	public static Calendar getCalendarWithZeroAtDetailAndBelow(TimeDetail detailAndBelow) {
		return setToZeroDetailAndBelow(Calendar.getInstance(), detailAndBelow);
	}
	
	
}