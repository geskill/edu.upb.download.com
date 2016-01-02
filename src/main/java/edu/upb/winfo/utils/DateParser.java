package edu.upb.winfo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by geskill on 01.01.2016.
 *
 * @author geskill
 */
public class DateParser {

	// "November 04, 2015"
	private static final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMMM dd, yyyy");

	public static Date getDate(String date) throws ParseException {

		return simpleDateFormat.parse(date);

	}
}
