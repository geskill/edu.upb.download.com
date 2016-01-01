package edu.upb.winfo.download.com;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by geskill on 01.01.2016.
 *
 * @author geskill
 */
public class RegEx {

	public static String getMatch(Pattern pattern, CharSequence input) {

		Matcher m = pattern.matcher(input);
		if (m.find()) {
			return m.group(1);
		}
		return "";
	}

}
