package com.tantian.bopModel;

import java.io.File;
import java.util.List;

/**
 * ��
 *
 * @author hspcadmin
 *
 */
public class BopClass {
	// ����
	private String className;
	// ·��
	private String classPath;
	// �ļ�
	private File classFile;
	// ���Լ��ķ���
	private List<BopMethod> bopSelfMethods;
	// ��ʵ�ֵĽӿ�
	private List<BopInterface> bopImplementsInterface;
	// ��̳е���
	private List<BopClass> bopImplementsClass;
	// �����õ�Bean
	private List<BopClass> bopQuoteClass;
	// ����õķ���
	private List<BopMethod> bopInvokeMethods;
}
