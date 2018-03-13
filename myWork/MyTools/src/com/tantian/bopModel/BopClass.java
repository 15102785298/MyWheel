package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

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
	private Class classClass;
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
	private String fileContant = "";

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public File getClassFile() {
		return classFile;
	}

	public void setClassFile(File classFile) {
		this.classFile = classFile;
	}

	public Class getClassClass() {
		return classClass;
	}

	public void setClassClass(Class classClass) {
		this.classClass = classClass;
	}

	public List<BopMethod> getBopSelfMethods() {
		return bopSelfMethods;
	}

	public void setBopSelfMethods(List<BopMethod> bopSelfMethods) {
		this.bopSelfMethods = bopSelfMethods;
	}

	public List<BopInterface> getBopImplementsInterface() {
		return bopImplementsInterface;
	}

	public void setBopImplementsInterface(List<BopInterface> bopImplementsInterface) {
		this.bopImplementsInterface = bopImplementsInterface;
	}

	public List<BopClass> getBopImplementsClass() {
		return bopImplementsClass;
	}

	public void setBopImplementsClass(List<BopClass> bopImplementsClass) {
		this.bopImplementsClass = bopImplementsClass;
	}

	public List<BopClass> getBopQuoteClass() {
		return bopQuoteClass;
	}

	public void setBopQuoteClass(List<BopClass> bopQuoteClass) {
		this.bopQuoteClass = bopQuoteClass;
	}

	public List<BopMethod> getBopInvokeMethods() {
		return bopInvokeMethods;
	}

	public void setBopInvokeMethods(List<BopMethod> bopInvokeMethods) {
		this.bopInvokeMethods = bopInvokeMethods;
	}

	public String getFileContant() {
		return fileContant;
	}

	public void setFileContant(String fileContant) {
		this.fileContant = fileContant;
	}

	public BopClass(File javaFile, Class<?> tempClass) {
		this.classFile = javaFile;
		this.className = StringUtils.split(classFile.getName(), ".")[0];
		this.classPath = classFile.getAbsolutePath();
		this.classClass = tempClass;
		try {
			this.fileContant = reader(javaFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.bopSelfMethods = getAllSelfMethod(classClass, classFile);

	}

	public void printfSelf() {
		System.out.println("�����ƣ�" + className);
		System.out.println("��·����" + classPath);
		System.out.println("-------------��" + className + "�еķ���-----------");
		for (BopMethod temp : bopSelfMethods) {
			temp.printfSelf();
		}
		System.out.println("-------------��" + className + "�еķ���-----------");
	}

	/**
	 * ��ȡ���еķ���
	 *
	 * @param interfaceClass
	 * @param interfaceFile2
	 * @return
	 */
	private List<BopMethod> getAllSelfMethod(Class<?> interfaceClass, File interfaceFile2) {
		List<BopMethod> res = new LinkedList<>();
		for (Method temp : interfaceClass.getDeclaredMethods()) {
			res.add(new BopMethod(this, "", temp));
		}
		return res;
	}

	private String reader(File interfaceFile2) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(interfaceFile2), "UTF-8");
		BufferedReader br = new BufferedReader(reader);
		String line = "";
		line = br.readLine();
		boolean isOut = false;
		while (line != null) {
			line = line.trim();
			if (StringUtils.startsWith(line, "//")) {
				line = br.readLine();
				continue;
			}
			if (StringUtils.startsWith(line, "/*")) {
				isOut = true;
			}
			if (StringUtils.endsWith(line, "*/")) {
				isOut = false;
			}
			if (isOut) {
				line = br.readLine();
				continue;
			}
			sb.append(line).append(" ");
			line = br.readLine();
		}
		return sb.toString();
	}
}
