package edu.upb.winfo.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by geskill on 01.01.2016.
 *
 * @author geskill
 */
public class RegEx {

	/**
	 * This is just some function to simplify the regular expression usage.
	 * We are always interested in the first group of the match. Additionally
	 * we do some timing of line spaces.
	 *
	 * @param pattern The regular expression pattern
	 * @param input The to be searched string
	 * @return true, if found
	 */
	public static String getMatch(Pattern pattern, CharSequence input) {

		Matcher m = pattern.matcher(input);
		if (m.find()) {
			return m.group(1).trim();
		}
		return "";
	}

}
