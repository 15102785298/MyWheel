package com.tantian.bopModel;

public class ServiceImpleMethod {
	// ��Ӧ������
	private String serviceName;
	// ��Ӧ�ķ�����
	private String methodName;

	public ServiceImpleMethod(String serviceName, String methodName) {
		this.serviceName = serviceName;
		this.methodName = methodName;
	}

	public void printfSelf() {
		System.out.println(serviceName + "." + methodName);
	}

}
