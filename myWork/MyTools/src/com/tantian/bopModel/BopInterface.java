package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopUtils.PatternUtils;

public class BopInterface {
	// �ӿ�����
	private String interfaceName;
	// �ӿ�·��
	private String interfacePath;
	// �ӿ��ļ�
	private File interfaceFile;
	// �ӿ���
	private Class interfaceClass;
	// �ӿڱ�¶�ķ���
	private List<BopMethod> interfaceMethods;
	// �ļ�����
	private String fileContant = "";

	public String getInterfaceName() {
		return interfaceName;
	}

	public void setInterfaceName(String interfaceName) {
		this.interfaceName = interfaceName;
	}

	public String getInterfacePath() {
		return interfacePath;
	}

	public void setInterfacePath(String interfacePath) {
		this.interfacePath = interfacePath;
	}

	public File getInterfaceFile() {
		return interfaceFile;
	}

	public void setInterfaceFile(File interfaceFile) {
		this.interfaceFile = interfaceFile;
	}

	public Class getInterfaceClass() {
		return interfaceClass;
	}

	public void setInterfaceClass(Class interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

	public List<BopMethod> getInterfaceMethods() {
		return interfaceMethods;
	}

	public void setInterfaceMethods(List<BopMethod> interfaceMethods) {
		this.interfaceMethods = interfaceMethods;
	}

	public String getFileContant() {
		return fileContant;
	}

	public void setFileContant(String fileContant) {
		this.fileContant = fileContant;
	}

	public BopInterface(File interfaceFile, Class<?> interfaceClass) {
		this.interfaceFile = interfaceFile;
		this.interfaceName = StringUtils.split(interfaceFile.getName(), ".")[0];
		this.interfacePath = interfaceFile.getAbsolutePath();
		this.interfaceClass = interfaceClass;
		try {
			this.fileContant = reader(interfaceFile);
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.interfaceMethods = getAllMethod(interfaceClass, interfaceFile);
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
				if(StringUtils.indexOf(line, "*/") < 0){
					isOut = true;
				}
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

	public void printfSelf() {
		System.out.println("�ӿ����ƣ�" + interfaceName);
		System.out.println("�ӿ�·����" + interfacePath);
		System.out.println("-------------�ӿ�" + interfaceName + "�еķ���-----------");
		for (BopMethod temp : interfaceMethods) {
			temp.printfSelf();
		}
		System.out.println("-------------�ӿ�" + interfaceName + "�еķ���-----------");
	}

	/**
	 * ��ȡ�ӿ��еķ���
	 *
	 * @param interfaceClass
	 * @param interfaceFile2
	 * @return
	 */
	private List<BopMethod> getAllMethod(Class<?> interfaceClass, File interfaceFile2) {
		List<BopMethod> res = new LinkedList<>();
		for (Method temp : interfaceClass.getDeclaredMethods()) {
			res.add(new BopMethod(this, "", temp));
		}
		return res;
	}

}
