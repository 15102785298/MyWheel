package com.tantian.bopModel;

import java.util.List;

public class ServiceImpleMethod {
	// 对应的类名
	private String serviceName;
	// 对应的方法名
	private String methodName;
	// 对应的实现类
	private Class<?> serviceImpl;

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
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

	public ServiceImpleMethod(String serviceName, String methodName, List<Class<?>> list) {
		this.serviceName = serviceName;
		this.methodName = methodName;
		this.serviceImpl = list.get(0);
		if (list.size() != 1 && !"MemberAddress,RandomShortUUID,IQualiArgService,CacheManager,DictManager,PermissionMenu".contains(serviceName)) {
			System.out.println();
		}

	}

	public void printfSelf() {
		System.out.println(serviceName + "." + methodName);
	}

}
