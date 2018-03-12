package com.tantian.test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * 此类表明了静态对象是对于某个class的所有实例起作用，不同的class可以申请同名静态变量，不会相互影响
 ***/
public class testStatic {

	public static int aa = 1;
	private static void getStrings() {
        String str = "D:\\zt\\BOP\\Sources\\WebCodes\\bop2.0\\bop-acct\\bop-view\\bop-view-acct\\src\\main\\webapp\\WEB-INF\\views\\screen\\bop\\acpt\\biz\\acct\\applyProf.vm";
        Pattern p = Pattern.compile("\\\\bop\\\\(\\S*).vm");
        Matcher m = p.matcher(str);
        ArrayList<String> strs = new ArrayList<String>();
        int a = 0;
        while (m.find()) {
            strs.add(m.group(a++));
        }
        for (String s : strs){
            System.out.println(s);
        }
    }
	public static void main(String[] args) {
		getStrings();
	}

}
