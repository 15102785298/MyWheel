package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
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
	// �ӿڱ�¶�ķ���
	private List<BopMethod> interfaceMethods;
	// �ļ�����
	private String fileContant = "";

	public BopInterface(File interfaceFile) {
		this.interfaceFile = interfaceFile;
		this.interfaceName = StringUtils.split(interfaceFile.getName(), ".")[0];
		this.interfacePath = interfaceFile.getAbsolutePath();
		this.interfaceMethods = getInterfaceMethods(interfaceFile);
	}

	private List<BopMethod> getInterfaceMethods(File interfaceFile) {
		List<BopMethod> res = new LinkedList<>();
		// ���û������������ڴ�
		if (fileContant == "") {
			StringBuffer sb = new StringBuffer();
			try {
				// ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
				File filename = interfaceFile;
				// ����������׼����ȡ
				InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8");
				// ����һ�����������ļ�����ת�ɼ�����ܶ���������
				BufferedReader br = new BufferedReader(reader);
				String line = "";
				boolean isNoEffit = false;
				line = br.readLine();
				while (line != null) {
					line = line.trim();
					if (StringUtils.startsWith(line, "//")) {
						line = br.readLine();
						continue;
					}
					if (StringUtils.startsWith(line, "/*")) {
						line = br.readLine();
						isNoEffit = true;
					}
					if (isNoEffit) {
						if (StringUtils.endsWith(line, "*/")) {
							isNoEffit = false;
						}
						line = br.readLine();
						continue;
					}
					sb.append(line);
					sb.append(" ");
					// һ�ζ���һ������
					line = br.readLine();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			fileContant = sb.toString();
		}
		List<String> methodSplitList = new LinkedList<>();
		System.out.println(fileContant);
		StringBuffer nowStr = new StringBuffer();
		boolean addFlag = false;
		// ���ֽڶ�
		for (char temp : fileContant.toCharArray()) {
			if (temp == '(') {
				nowStr.setLength(0);
				addFlag = true;
			}
			if (temp == ')') {
				methodSplitList.add(nowStr.toString());
				addFlag = false;
			}
			if (addFlag) {
				nowStr.append(temp);
			}
		}
		return res;

	}

}
