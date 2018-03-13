package com.tantian.bopModel;

public class ServiceImpleMethod {
	// 对应的类名
	private String serviceName;
	// 对应的方法名
	private String methodName;

	public ServiceImpleMethod(String serviceName, String methodName) {
		this.serviceName = serviceName;
		this.methodName = methodName;
	}

	public void printfSelf() {
		System.out.println(serviceName + "." + methodName);
	}

}
