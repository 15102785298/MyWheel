package com.tantian.bopUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PatternUtils {
	public static List<String> patternString(String mainString, String regex, int index) {
		List<String> res = new LinkedList<>();
		Pattern pattern = Pattern.compile(regex);
		Matcher macher = pattern.matcher(mainString);
		while (macher.find()) {
			res.add(macher.group(index));
		}
		return res;
	}
}
