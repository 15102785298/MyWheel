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
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopUtils.PatternUtils;

public class BopInterface {
	// 接口名称
	private String interfaceName;
	// 接口路径
	private String interfacePath;
	// 接口文件
	private File interfaceFile;
	// 接口类
	private Class interfaceClass;
	// 接口暴露的方法
	private List<BopMethod> interfaceMethods;
	// 文件内容
	private String fileContant = "";
	// 实现类
	private Class<?> serviceImpl;

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

	public BopInterface(File interfaceFile, Class<?> interfaceClass, Map<String, Class<?>> allClass) {
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
		List<Class<?>> list = findImpl(allClass, interfaceClass);
		for (Class<?> temp : list) {
			if (!temp.isInterface()) {
				this.serviceImpl = temp;
			}
		}
	}

	private List<Class<?>> findImpl(Map<String, Class<?>> allClasses, Class<?> interfaceClass) {
		List<Class<?>> res = new LinkedList<>();
		for (Entry<String, Class<?>> temp : allClasses.entrySet()) {
			Class<?> tempClass = temp.getValue();
			if (tempClass.getTypeName().equals(interfaceClass.getTypeName())) {
				res.add(tempClass);
			}
			for (Class<?> tempInterface : tempClass.getInterfaces()) {
				if (tempInterface.getTypeName().equals(interfaceClass.getTypeName())) {
					res.add(tempClass);
				}
			}
		}
		if (res.size() == 0) {
			System.out.println(interfaceClass.getName() + "查找实现类失败");
		}
		return res;
	}

	private String reader(File interfaceFile2) throws IOException {
		StringBuffer sb = new StringBuffer();
		InputStreamReader reader = new InputStreamReader(new FileInputStream(interfaceFile2), "UTF-8");
		BufferedReader br = new BufferedReader(reader);
		String line = "";
		line = br.readLine();
		Stack<String> stack = new Stack<>();
		boolean isOut = false;
		while (line != null) {
			line = line.trim();
			if (StringUtils.startsWith(line, "//")) {
				line = br.readLine();
				continue;
			}
			if (StringUtils.indexOf(line, "/*") > -1) {
				sb.append(StringUtils.substringBeforeLast(line, "/*"));
				if (StringUtils.indexOf(line, "*/") > -1) {
					sb.append(StringUtils.substringAfterLast(line, "*/").trim().indexOf("//") > -1 ? ""
							: StringUtils.substringAfterLast(line, "*/").trim());
				} else {
					isOut = true;
				}
				line = br.readLine();
				continue;
			}
			if (StringUtils.indexOf(line, "*/") > -1) {
				isOut = false;
				sb.append(StringUtils.substringAfterLast(line, "*/").trim().indexOf("//") > -1 ? ""
						: StringUtils.substringAfterLast(line, "*/").trim());
				line = br.readLine();
				continue;
			}
			if (isOut) {
				line = br.readLine();
				continue;
			}
			if (StringUtils.startsWith(line, "//")) {
				line = br.readLine();
				continue;
			}
			if (StringUtils.indexOf(line, "//") > -1) {
				int indexFirst = StringUtils.indexOf(line, "\"");
				int indexLast = StringUtils.lastIndexOf(line, "\"");
				int com = StringUtils.indexOf(line, "//");
				if (indexFirst < com && indexLast > com) {
					sb.append(line);
				} else {
					sb.append(StringUtils.substringBefore(line, "//"));
				}
				line = br.readLine();
				continue;
			}
			sb.append(line).append(" ");
			line = br.readLine();
		}
		return sb.toString();
	}

	public Class<?> getServiceImpl() {
		return serviceImpl;
	}

	public void setServiceImpl(Class<?> serviceImpl) {
		this.serviceImpl = serviceImpl;
	}

	public void printfSelf(Map<String, BopClass> bopActionMap) {
		System.out.println("接口名称：" + interfaceName);
		System.out.println("接口路径：" + interfacePath);
		if (serviceImpl != null) {
			System.out.println("接口实现类名称：" + serviceImpl.getName());
		}
		System.out.println("-------------接口" + interfaceName + "中的方法-----------");
		for (BopMethod temp : interfaceMethods) {
			temp.printfSelf(bopActionMap);
		}
		System.out.println("-------------接口" + interfaceName + "中的方法-----------");
	}

	/**
	 * 获取接口中的方法
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
