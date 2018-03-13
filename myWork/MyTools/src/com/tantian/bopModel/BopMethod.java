package com.tantian.bopModel;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.Policy.Parameters;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang3.StringUtils;

public class BopMethod {
	private boolean belongInterface = false;
	private boolean isPrivateMethod = false;
	// 方法对应的类
	private BopClass methodClass;
	// 方法对应的接口
	private BopInterface methodInterface;
	// 方法对应的方法名称
	private String methodName;
	// 方法体中调用的方法
	private List<ServiceImpleMethod> invokeMethods;
	// 入参
	private Parameter[] bopInParams;
	// 框架方法体
	private Method method;
	private int bodyBegin = -1;
	private int bodyEnd = -1;
	private String bodyStr = "";

	public BopMethod(BopClass methodClass, String methodBody, Method method) {
		this.belongInterface = false;
		this.methodClass = methodClass;
		this.methodName = method.getName();
		this.bopInParams = method.getParameters();
		this.method = method;
		this.isPrivateMethod = StringUtils.contains(Modifier.toString(method.getModifiers()), "private");
		String nowFileString = belongInterface ? methodInterface.getFileContant() : methodClass.getFileContant();
		this.bodyStr = getBodyString(nowFileString);
		this.invokeMethods = getAllIncokeMethods();
		if (methodName.equals("getCsdcChanged")) {
			System.out.println("dfd");
		}
	}

	private String getBodyString(String nowFileString) {
		bodyBegin = nowFileString.indexOf(methodName + "(");
		if (bodyBegin == -1) {
			bodyBegin = nowFileString.indexOf(methodName + " ");
		}
		String lastStr = StringUtils.substring(nowFileString, bodyBegin);
		StringBuffer sb = new StringBuffer();
		int count = 0;
		boolean canFinish = false;
		int isMethod = 0;
		char want = '(';
		for (char in : lastStr.toCharArray()) {
			if (isIn(in)) {
				if (want != '+') {
					if (want != in) {
						return getBodyString(StringUtils.substring(lastStr, methodName.length()));
					}
					switch (want) {
					case '(':
						want = ')';
						break;
					case ')':
						want = '{';
						break;
					case '{':
						want = '+';
					default:
						break;
					}
				}
			}
			if (in == '{') {
				count++;
			}
			sb.append(in);
			if (in == '}') {
				count--;
			}
			if (count != 0) {
				canFinish = true;
			}
			if (canFinish && count == 0) {
				bodyEnd = bodyBegin + sb.toString().length() - 1;
				return sb.toString();
			}
		}
		return "";

	}

	private boolean isIn(char in) {
		if (in == '(' || in == ')' || in == '{' || in == '}') {
			return true;
		}
		return false;
	}

	public BopMethod(BopInterface methodInterface, String methodBody, Method method) {
		this.belongInterface = true;
		this.methodInterface = methodInterface;
		this.methodName = method.getName();
		this.bopInParams = method.getParameters();
		this.method = method;
		this.isPrivateMethod = StringUtils.contains(Modifier.toString(method.getModifiers()), "private");
		String nowFileString = belongInterface ? methodInterface.getFileContant() : methodClass.getFileContant();
		this.bodyStr = getBodyString(nowFileString);
		this.invokeMethods = getAllIncokeMethods();

	}

	public List<ServiceImpleMethod> getAllIncokeMethods() {
		List<ServiceImpleMethod> res = new LinkedList<>();
		Class<?> nowClass = belongInterface ? methodInterface.getInterfaceClass() : methodClass.getClassClass();
		Map<String, Class<?>> inClass = new HashMap<>();
		for (Field temp : nowClass.getDeclaredFields()) {
			if (isDeclared(temp)) {
				inClass.put(temp.getName(), temp.getType());
			}
		}
		for (Entry<String, Class<?>> temp : inClass.entrySet()) {
			if (StringUtils.indexOf(bodyStr, temp.getKey() + ".") > -1) {
				String tempStr = StringUtils.substring(bodyStr, StringUtils.indexOf(bodyStr, temp.getKey() + "."));
				try {
					res.add(new ServiceImpleMethod(
							StringUtils.substring(temp.getValue().getTypeName(),
									StringUtils.lastIndexOf(temp.getValue().getTypeName(), ".") + 1),
							StringUtils.split(StringUtils.split(tempStr, "(")[0], ".")[1]));
				} catch (Exception e) {
					System.out.println(tempStr);
				}
			}
		}
		return res;
	}

	private static boolean isDeclared(Field tempClass) {
		for (Annotation temp : tempClass.getAnnotations()) {
			if (StringUtils.endsWith(temp.annotationType().getName(), "Autowired")) {
				return true;
			}
		}
		return false;
	}

	public void printfSelf() {
		System.out.println("方法名：" + methodName);
		System.out.println("是否属于接口：" + (belongInterface ? "是" : "否"));
		System.out.println("是否是私有方法：" + (isPrivateMethod ? "是" : "否"));
		System.out.println("---------------方法体" + methodName + "中调用的方法----------");
		for (ServiceImpleMethod temp : this.invokeMethods) {
			temp.printfSelf();
		}
		System.out.println("---------------方法体" + methodName + "中调用的方法----------");
	}
}
