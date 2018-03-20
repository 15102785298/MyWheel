package com.tantian.bopModel;

import java.util.List;

public class ServiceImpleMethod {
	// ��Ӧ������
	private String serviceName;
	// ��Ӧ�ķ�����
	private String methodName;
	// ��Ӧ��ʵ����
	private Class<?> serviceImpl;
	private int paramCount = 0;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getParamCount() {
		return paramCount;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public Class<?> getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceImpl(Class<?> serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	@Override
	public boolean equals(Object a) {
		ServiceImpleMethod temp = (ServiceImpleMethod) a;
		if (this.serviceName.equals(temp.serviceName) && this.methodName.equals(temp.methodName)) {
			return true;
		}
		return false;
	}

	public ServiceImpleMethod(String serviceName, String methodName, List<Class<?>> list, int inParamCount) {
		this.serviceName = serviceName.trim();
		this.methodName = methodName.trim();
		this.paramCount = inParamCount;
		for (Class<?> temp : list) {
			if (!temp.isInterface()) {
				this.serviceImpl = temp;
			}
		}
		if (this.serviceImpl == null) {
			System.out.println("��������ʱδ�ҵ�ʵ����,�ӿ�Ϊ." + serviceName);
		}

	}

	public void printfSelf() {
		System.out.println(serviceName + "." + methodName);
	}

}
