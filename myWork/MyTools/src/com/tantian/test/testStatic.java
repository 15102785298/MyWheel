package com.tantian.test;

/***
 * ��������˾�̬�����Ƕ���ĳ��class������ʵ�������ã���ͬ��class��������ͬ����̬�����������໥Ӱ��
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
