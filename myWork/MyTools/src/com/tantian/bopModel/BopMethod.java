package com.tantian.bopModel;

import java.util.List;

public class BopMethod {
	// 方法对应的类
	private BopClass methodClass;
	// 方法对应的方法名称
	private String methodName;
	// 方法体中调用的方法
	private List<BopMethod> invokeMethods;
	// 入参
	private List<BopMethod> bopInParams;

	public BopMethod(BopClass methodClass, String methodName, String bopInParamsStr, String invokeMethodsStr) {
	}
}
