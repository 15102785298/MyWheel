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
	// 接口名称
	private String interfaceName;
	// 接口路径
	private String interfacePath;
	// 接口文件
	private File interfaceFile;
	// 接口暴露的方法
	private List<BopMethod> interfaceMethods;
	// 文件内容
	private String fileContant = "";

	public BopInterface(File interfaceFile) {
		this.interfaceFile = interfaceFile;
		this.interfaceName = StringUtils.split(interfaceFile.getName(), ".")[0];
		this.interfacePath = interfaceFile.getAbsolutePath();
		this.interfaceMethods = getInterfaceMethods(interfaceFile);
	}

	private List<BopMethod> getInterfaceMethods(File interfaceFile) {
		List<BopMethod> res = new LinkedList<>();
		// 如果没读过，则读入内存
		if (fileContant == "") {
			StringBuffer sb = new StringBuffer();
			try {
				// 防止文件建立或读取失败，用catch捕捉错误并打印，也可以throw
				File filename = interfaceFile;
				// 建立流对象，准备读取
				InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8");
				// 建立一个对象，它把文件内容转成计算机能读懂的语言
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
					// 一次读入一行数据
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
		// 逐字节读
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
