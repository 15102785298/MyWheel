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
import java.util.Stack;

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
	private List<Integer> localRefList = null;

	public boolean isBelongInterface() {
		return belongInterface;
	}

	public void setBelongInterface(boolean belongInterface) {
		this.belongInterface = belongInterface;
	}

	public boolean isPrivateMethod() {
		return isPrivateMethod;
	}

	public void setPrivateMethod(boolean isPrivateMethod) {
		this.isPrivateMethod = isPrivateMethod;
	}

	public BopClass getMethodClass() {
		return methodClass;
	}

	public void setMethodClass(BopClass methodClass) {
		this.methodClass = methodClass;
	}

	public BopInterface getMethodInterface() {
		return methodInterface;
	}

	public void setMethodInterface(BopInterface methodInterface) {
		this.methodInterface = methodInterface;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<ServiceImpleMethod> getInvokeMethods() {
		return invokeMethods;
	}

	public void setInvokeMethods(List<ServiceImpleMethod> invokeMethods) {
		this.invokeMethods = invokeMethods;
	}

	public Parameter[] getBopInParams() {
		return bopInParams;
	}

	public void setBopInParams(Parameter[] bopInParams) {
		this.bopInParams = bopInParams;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public int getBodyBegin() {
		return bodyBegin;
	}

	public void setBodyBegin(int bodyBegin) {
		this.bodyBegin = bodyBegin;
	}

	public int getBodyEnd() {
		return bodyEnd;
	}

	public void setBodyEnd(int bodyEnd) {
		this.bodyEnd = bodyEnd;
	}

	public String getBodyStr() {
		return bodyStr;
	}

	public void setBodyStr(String bodyStr) {
		this.bodyStr = bodyStr;
	}

	public List<Integer> getLocalRefList() {
		return localRefList;
	}

	public void setLocalRefList(List<Integer> localRefList) {
		this.localRefList = localRefList;
	}

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

	private String getBodyString(String nowFileString) {
		if (StringUtils.equals(methodName, "pricingEntrust") && StringUtils.equals("ProdAtomServiceImpl",
				belongInterface ? methodInterface.getInterfaceName() : methodClass.getClassName())) {
			System.out.println();
		}
		bodyBegin = nowFileString.indexOf(" " + methodName + "(");
		if (bodyBegin == -1) {
			bodyBegin = nowFileString.indexOf(" " + methodName + " ");
		}
		if (bodyBegin == -1) {
			bodyBegin = nowFileString.indexOf(">" + methodName + " ");
		}
		String lastStr = StringUtils.substring(nowFileString, bodyBegin);
		StringBuffer sb = new StringBuffer();
		int count = 0;
		boolean throwsMethod = false;
		boolean canFinish = false;
		boolean canFinish2 = false;
		boolean canFinish3 = false;
		Stack<Character> want = new Stack<>();
		int length = lastStr.length();
		for (int i = 0; i < length; i++) {
			char in = lastStr.charAt(i);
			if (!canFinish2 && isIn(in)) {
				if (!want.empty() && getWant(want.peek()).equals(in)) {
					want.pop();
				} else {
					want.push(in);
				}
				if (want.isEmpty()) {
					sb.append(in);
					canFinish2 = true;
					continue;
				}
			}
			if (throwsMethod && in == '{') {
				throwsMethod = false;
			}
			if(throwsMethod){
				sb.append(in);
				continue;
			}
			// 方法入参匹配完成
			// 检查是否存异常声明
			if (canFinish2 && !canFinish3) {
				if (in == ' ') {
					continue;
				} else {
					if (in != '{') {
						// 如果括号后是'；'肯定是方法
						if (in == ';') {
							return getBodyString(StringUtils.substring(lastStr, methodName.length()));
						}
						if (in == 't' && lastStr.charAt(i + 1) == 'h' && lastStr.charAt(i + 2) == 'r'
								&& lastStr.charAt(i + 3) == 'o' && lastStr.charAt(i + 4) == 'w'
								&& lastStr.charAt(i + 5) == 's') {
							throwsMethod = true;
						} else {
							return getBodyString(StringUtils.substring(lastStr, methodName.length()));
						}
					} else {
						canFinish3 = true;
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

	private Character getWant(Character peek) {
		switch (peek) {
		case '(':
			return ')';
		case '{':
			return '}';
		default:
			return '*';
		}

	}

	private boolean isIn(char in) {
		if (in == '(' || in == ')' || in == '{' || in == '}') {
			return true;
		}
		return false;
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

	public BopMethod printfSelf() {
		 System.out.println("--------------方法." + methodName + "属性------------------------");
		 System.out.println("类起始位置：" + bodyBegin + "-" + bodyEnd);
		 System.out.println("是否属于接口：" + (belongInterface ? "是" : "否"));
		if (!belongInterface && !(methodName.startsWith("get") || methodName.startsWith("set"))
				&& (bodyBegin == -1 || bodyEnd == -1)) {
			return this;

		}
		System.out.println("是否是私有方法：" + (isPrivateMethod ? "是" : "否"));
		if(!this.invokeMethods.isEmpty()){
			System.out.println("---------------方法体" + methodName + "中调用的方法----------");
			for (ServiceImpleMethod temp : this.invokeMethods) {
				temp.printfSelf();
			}
			System.out.println("---------------方法体" + methodName + "中调用的方法----------");
		}
		return null;
	}

}
