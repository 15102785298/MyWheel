package com.tantian.test;

/***
 * 此类表明了静态对象是对于某个class的所有实例起作用，不同的class可以申请同名静态变量，不会相互影响
 ***/
public class testStatic {

	public static int aa = 1;

	public static void main(String[] args) {
		obj oo = new obj();
		System.out.println(obj.aa);
		aa = 100;
		System.out.println(aa);
		oo.aa = 99;
		System.out.println(oo.aa);
		obj oo2 = new obj();
		System.out.println(oo2.aa);
	}

}
