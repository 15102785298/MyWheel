package com.tantian.bopModel;

import java.util.List;

public class BopMethod {
	// ������Ӧ����
	private BopClass methodClass;
	// ������Ӧ�ķ�������
	private String methodName;
	// �������е��õķ���
	private List<BopMethod> invokeMethods;
	// ���
	private List<BopMethod> bopInParams;

	public BopMethod(BopClass methodClass, String methodName, String bopInParamsStr, String invokeMethodsStr) {
	}
}
