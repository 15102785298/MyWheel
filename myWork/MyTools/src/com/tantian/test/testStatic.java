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
		String aa = "				result = delProduct(product[0], product[1], product[2], product[3]);";
		Pattern pattern = Pattern.compile("([^\\.]\\s)+(\\b)" + "delProduct" + "(\\s*\\(|\\()");
		Matcher macher = pattern.matcher(aa);
		while (macher.find())
			System.out.println();
	}

	public static void main(String[] args) {
		getStrings();
	}

}
