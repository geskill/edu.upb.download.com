package edu.upb.winfo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by geskill on 01.01.2016.
 *
 * @author geskill
 */
public class DateParser {

	// "November 04, 2015"
	// INFO: Locale is US
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM dd, yyyy", Locale.US);

	/**
	 * Converts a download.cnet.com date format string into a java date format.
	 *
	 * @param date The download.cnet.com date format string
	 * @return The date in java date format
	 * @throws ParseException
	 */
	public static Date getDate(String date) throws ParseException {

		return simpleDateFormat.parse(date);

	}
}
