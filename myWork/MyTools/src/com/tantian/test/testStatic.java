package com.tantian.test;

import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopUtils.BopClassLoaderUtils;
import com.tantian.bopUtils.FileUtils;

/***
 * ��������˾�̬�����Ƕ���ĳ��class������ʵ�������ã���ͬ��class��������ͬ����̬�����������໥Ӱ��
 ***/
public class testStatic {

	public static int aa = 1;

	private static void getStrings() {
		String str = "public static void main(String[] args)"
				+ " public static void main(String[] args)public static void main(String[] args)";
		String ragex = "((\\b)+static(\\b)+)|((\\b)+main(\\b)+)";
		Pattern p = Pattern.compile(ragex);
		Matcher m = p.matcher(str);
		while (m.find()) {
			String s = m.group();
			System.out.println(s);
		}
	}

	public static void main(String[] args) {
		getStrings();
	}

}
