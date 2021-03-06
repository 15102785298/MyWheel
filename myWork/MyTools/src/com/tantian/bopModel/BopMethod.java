package com.tantian.bopModel;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.security.Policy.Parameters;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Stack;

import org.apache.catalina.connector.Request;
import org.apache.commons.lang3.StringUtils;

public class BopMethod {

	public static boolean fastModel = false;

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
	private int paramCount = 0;
	private String bodyStr = "";
	private String inParamStr = "";
	private Set<Integer> localRefList = null;
	private Set<BopMethod> beRefList = new HashSet<>();
	private Set<BopMethod> refList = new HashSet<>();
	private Set<BopMethod> refListAll = new HashSet<>();
	private String url = "";
	private Set<String> functionSet = new HashSet<>();
	private Set<String> functionSetAll = new HashSet<>();
	private String serviceId = "";

	public static boolean isFastModel() {
		return fastModel;
	}

	public static void setFastModel(boolean fastModel) {
		BopMethod.fastModel = fastModel;
	}

	public int getParamCount() {
		return paramCount;
	}

	public void setParamCount(int paramCount) {
		this.paramCount = paramCount;
	}

	public Set<String> getFunctionSet() {
		return functionSet;
	}

	public void setFunctionSet(Set<String> functionSet) {
		this.functionSet = functionSet;
	}

	public Set<String> getFunctionSetAll() {
		return functionSetAll;
	}

	public void setFunctionSetAll(Set<String> functionSetAll) {
		this.functionSetAll = functionSetAll;
	}

	public void functionSetAllAdd(Set<String> aim) {
		functionSetAll.addAll(aim);
	}

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

	public Parameter[] getBopInParams() {
		return bopInParams;
	}

	public String getInParamStr() {
		return inParamStr;
	}

	public void setInParamStr(String inParamStr) {
		this.inParamStr = inParamStr;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public void setRefListAll(Set<BopMethod> refListAll) {
		this.refListAll = refListAll;
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

	public List<ServiceImpleMethod> getInvokeMethods() {
		return invokeMethods;
	}

	public void setInvokeMethods(List<ServiceImpleMethod> invokeMethods) {
		this.invokeMethods = invokeMethods;
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

	public Set<Integer> getLocalRefList() {
		return localRefList;
	}

	public void setLocalRefList(Set<Integer> localRefList) {
		this.localRefList = localRefList;
	}

	public BopMethod(BopClass methodClass, String methodBody, Method method, List<BopMethod> nowMethodList,
			Map<String, Class<?>> allClasses, List<String> functionName, List<String> function)
			throws ClassNotFoundException {
		this.belongInterface = false;
		this.methodClass = methodClass;
		this.methodName = method.getName();
		this.bopInParams = method.getParameters();
		this.method = method;
		for (Annotation temp : method.getAnnotations()) {
			if (temp.annotationType().getTypeName().equals("org.springframework.web.bind.annotation.RequestMapping")) {
				Method[] methList = temp.annotationType().getDeclaredMethods();
				try {
					this.url = ((String[]) methList[0].invoke(temp))[0];
					// System.out.println("方法对应url." + getMethodName() + " -
					// url: " + this.url);
				} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}
		this.isPrivateMethod = StringUtils.contains(Modifier.toString(method.getModifiers()), "private");
		String nowFileString = belongInterface ? methodInterface.getFileContant() : methodClass.getFileContant();
		String anaString = nowFileString;
		this.bodyStr = getBodyString(anaString);
		this.invokeMethods = getAllIncokeMethods(allClasses);
		this.localRefList = calcuteLocalRefList();
		this.paramCount = method.getParameterCount();
		while ((!analize()) && this.bodyStr != "") {
			anaString = StringUtils.substring(anaString, anaString.indexOf(this.bodyStr) + methodName.length());
			this.bodyStr = getBodyString(anaString);
			this.invokeMethods = getAllIncokeMethods(allClasses);
			this.localRefList = calcuteLocalRefList();
		}
		if (functionName.size() > 0) {
			this.functionSet = getFunctionSet(functionName, function);
		} else {
			this.functionSet = new HashSet<>();
		}

	}

	private Set<String> getFunctionSet(List<String> functionName, List<String> function) {
		Set<String> res = new HashSet<>();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < functionName.size(); i++) {
			sb.setLength(0);
			sb.append("(\\b)+").append(functionName.get(i)).append("(\\b)+");
			Pattern pattern = Pattern.compile(sb.toString());
			Matcher macher = pattern.matcher(this.bodyStr);
			if (macher.find()) {
				System.out.println(this.getMethodName() + "方法存在常量." + functionName.get(i));
				res.add(function.get(i));
			}
		}
		return res;
	}

	private boolean analize() {
		List<String> resList = new LinkedList<>();
		Stack<Character> stackOp = new Stack<>();
		StringBuffer sb = new StringBuffer();
		for (char in : this.inParamStr.toCharArray()) {
			if (InPar(in)) {
				if (in == '(' || in == '<') {
					stackOp.push(in);
					resList.add(sb.toString());
					sb.setLength(0);
					sb.append(in);
				} else {
					if (in == ')') {
						if (stackOp.peek() == '(') {
							stackOp.pop();
							sb.append(in);
							resList.add(sb.toString());
							sb.setLength(0);
							if (stackOp.isEmpty()) {
								break;
							}
						}
					}
					if (in == '>') {
						if (stackOp.peek() == '<') {
							stackOp.pop();
							sb.append(in);
							resList.add(sb.toString());
							sb.setLength(0);
						}
					}
				}
			} else {
				sb.append(in);
			}
		}

		Iterator<String> iterator = resList.iterator();
		while (iterator.hasNext()) {
			String now = iterator.next();
			if (StringUtils.contains(now, '<') || StringUtils.contains(now, '=')) {
				iterator.remove();
			}
		}
		StringBuffer finalSb = new StringBuffer();
		for (String temp : resList) {
			finalSb.append(temp).append(' ');
		}
		String aimMethod = finalSb.toString();
		String[] methodList = aimMethod.split(",");
		Parameter[] inParam = this.method.getParameters();
		if (inParam.length == 0) {
			if (!aimMethod.contains(",")) {
				return true;
			} else {
				return false;
			}
		}
		if (methodList.length != inParam.length) {
			return false;
		}
		for (int i = 0; i < methodList.length; i++) {
			Pattern pattern = Pattern.compile(
					"(\\b)+" + StringUtils.substringBefore(inParam[i].getType().getSimpleName(), "[") + "(\\b)+");
			Matcher macher = pattern.matcher(methodList[i]);
			if (!macher.find()) {
				return false;
			}
		}
		return true;

	}

	private boolean InPar(char in) {
		String temp = "()<>";
		return StringUtils.contains(temp, in);
	}

	private char getWantChar(char in) {
		switch (in) {
		case '(':
			return ')';
		case '<':
			return ')';
		default:
			return '*';
		}

	}

	private Set<Integer> calcuteLocalRefList() {
		this.bodyBegin = StringUtils.indexOf(this.getMethodClass().getFileContant(), " " + this.bodyStr);
		if (this.bodyBegin == -1) {
			this.bodyBegin = StringUtils.indexOf(this.getMethodClass().getFileContant(), ">" + this.bodyStr);
		}
		this.bodyEnd = this.bodyBegin + bodyStr.length();
		Set<Integer> res = new HashSet<>();
		Pattern pattern = Pattern.compile("((this[.])|([^.]\\b))" + methodName + "(\\b)(\\s*\\(|\\()");
		Matcher macher = pattern.matcher(methodClass.getFileContant());

		while (macher.find()) {
			int index = macher.start() + 1;
			if (bodyBegin > index || index > bodyEnd) {
				String opString = StringUtils.substring(methodClass.getFileContant(), index);
				opString = StringUtils.substringAfter(opString, "(");
				opString = StringUtils.substringBefore(opString, ")");
				StringBuffer sb = new StringBuffer();
				Stack<Character> opStack = new Stack<>();
				for (char in : opString.toCharArray()) {
					if (isIn(in)) {
						if (opStack.size() == 0) {
							opStack.push(in);
						}
						if (opStack.peek() == '(' && in == ')') {
							opStack.pop();
						}
						if (opStack.peek() == '{' && in == '}') {
							opStack.pop();
						}
					}
					if (opStack.size() == 0) {
						sb.append(in);
					}
				}
				int inParamCount = StringUtils.countMatches(sb.toString(), ",") + 1;
				if (inParamCount == this.getMethod().getParameterCount()) {
					res.add(index);
				}
			}
		}
		return res;
	}

	public BopMethod(BopInterface methodInterface, String methodBody, Method method) {
		this.belongInterface = true;
		this.methodInterface = methodInterface;
		this.methodName = method.getName();
		this.bopInParams = method.getParameters();
		this.method = method;
		this.isPrivateMethod = StringUtils.contains(Modifier.toString(method.getModifiers()), "private");
		// this.bodyStr = getBodyString(nowFileString);
		// this.invokeMethods = getAllIncokeMethods();
		this.paramCount = method.getParameterCount();
		getServiceId();
	}

	private String getServiceId() {
		for (Annotation temp : method.getAnnotations()) {
			if (temp.annotationType().getName().equals("com.hundsun.jresplus.remoting.impl.annotation.Service")) {
				Method[] methList = temp.annotationType().getDeclaredMethods();
				for (Method temp2 : methList) {
					if (temp2.getName().equals("functionId")) {
						try {
							this.serviceId = (String) temp2.invoke(temp);
						} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
							e.printStackTrace();
						}
					}
				}

			}
		}
		return null;
	}

	public BopMethod addInvokeMethods(BopMethod aim) {
		this.invokeMethods.addAll(aim.getInvokeMethods());
		return this;
	}

	public Set<BopMethod> getBeRefList() {
		return beRefList;
	}

	public void setBeRefList(Set<BopMethod> beRefList) {
		this.beRefList = beRefList;
	}

	public Set<BopMethod> getRefList() {
		return refList;
	}

	public Set<BopMethod> getRefListAll() {
		return refListAll;
	}

	public void setRefList(Set<BopMethod> refList) {
		this.refList = refList;
	}

	public void addBeRefItem(BopMethod i) {
		beRefList.add(i);
	}

	public void addRefItem(BopMethod i) {
		if (i == null) {
			return;
		}
		if (!i.getMethodName().equals(this.getMethodName())) {
			refList.add(i);
		}
	}

	public void addBeRefList(List<BopMethod> i) {
		beRefList.addAll(i);
	}

	public void addRefListAll(Set<BopMethod> i) {
		if (i == null) {
			return;
		}
		for (BopMethod temp : i) {
			// if (!temp.getMethodName().equals(this.getMethodName())) {
			refListAll.add(temp);
			// }
		}
	}

	public boolean isRefListEmpty() {
		if (this.refList != null && !this.refList.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public boolean invokeMethodsIsEmpty() {
		if (this.invokeMethods != null && !this.invokeMethods.isEmpty()) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	public boolean isInTheMethod(int index) {
		if (index == 26423) {
			System.out.println();
		}
		if (index < 0) {
			return Boolean.FALSE;
		}
		if (index >= bodyBegin && index <= bodyEnd) {
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

	private String getBodyString(String nowFileString) {

		Pattern pattern = Pattern.compile("(\\b)+" + methodName + "(\\s)*\\(");
		Matcher macher = pattern.matcher(nowFileString);
		if (!macher.find()) {
			System.out.println("出错---." + this.getMethodClass().getClassName() + "." + this.getMethodName());
			System.out.println(this.getMethodClass().getFileContant());
			return "";
		}
		bodyBegin = macher.start();
		if (bodyBegin == -1) {
			bodyBegin = nowFileString.indexOf(" " + methodName + " ");
		}
		if (bodyBegin == -1) {
			bodyBegin = nowFileString.indexOf(">" + methodName + " ");
		}
		String lastStr = StringUtils.substring(nowFileString, bodyBegin);
		StringBuffer sb = new StringBuffer();
		StringBuffer sb2 = new StringBuffer();
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
			if (!canFinish2) {
				sb2.append(in);
			}
			if (throwsMethod && in == '{') {
				throwsMethod = false;
			}
			if (throwsMethod) {
				sb.append(in);
				continue;
			}
			// 方法入参匹配完成
			// 检查是否存异常声明
			if (canFinish2 && !canFinish3) {
				if (in == ' ') {
					sb.append(in);
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
				this.inParamStr = sb2.append(")").toString();
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

	public List<ServiceImpleMethod> getAllIncokeMethods(Map<String, Class<?>> allClasses) {
		List<ServiceImpleMethod> res = new LinkedList<>();
		Class<?> nowClass = belongInterface ? methodInterface.getInterfaceClass() : methodClass.getClassClass();
		Map<String, Class<?>> inClass = new HashMap<>();
		for (Field temp : nowClass.getDeclaredFields()) {
			if (isDeclared(temp)) {
				inClass.put(temp.getName(), temp.getType());
			}
		}
		Pattern pattern = Pattern.compile("");
		StringBuffer sb = new StringBuffer();
		for (Entry<String, Class<?>> temp : inClass.entrySet()) {
			sb.setLength(0);
			sb.append("(\\b)+").append(temp.getKey()).append("(\\s)*([.])");
			pattern = Pattern.compile(sb.toString());
			Matcher macher = pattern.matcher(this.bodyStr);
			while (macher.find()) {
				String tempStr = StringUtils.substring(bodyStr, macher.start());
				String methodName = StringUtils.split(StringUtils.split(tempStr, "(")[0], ".")[1];
				StringBuffer sb2 = new StringBuffer();
				Stack<Character> opStack = new Stack<>();
				int inParamCount = 0;
				boolean waitFlag = false;
				for (char in : tempStr.toCharArray()) {
					if (!waitFlag && (in == '<' || in == '"')) {
						waitFlag = true;
						continue;
					}

					if (in == '>' || in == '"') {
						in = 'a';
						waitFlag = false;
					}
					// 换成非,
					if (waitFlag) {
						in = 'a';
					}
					if (isIn(in)) {
						if (opStack.size() == 0 || in == '(') {
							opStack.push(in);
							sb2.append(in);
							continue;
						}
						if (opStack.peek() == '(' && in == ')') {
							opStack.pop();
							sb2.append(in);
							if (opStack.isEmpty()) {
								break;
							}
							continue;
						}
					}
					if (opStack.size() == 1) {
						if (in == ',' && in != '(') {
							inParamCount++;
						}
						inParamCount = inParamCount == 0 ? 1 : inParamCount;
						sb2.append(in);
					}
				}
				try {
					res.add(new ServiceImpleMethod(temp.getValue().getTypeName(), methodName,
							findImpl(allClasses, temp.getValue()), inParamCount));
				} catch (Exception e) {
					System.out.println("出现异常！");
					System.out.println("未找到实现类." + tempStr);
				}
			}
		}
		return res;
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
			System.out.println("出现异常！");
			System.out.println(interfaceClass.getName() + "查找实现类失败");
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
		System.out.println("---方法：" + this.getMethodName() + "分析开始------");
		System.out.println("方法名：" + (belongInterface ? this.getMethodInterface().getInterfaceName()
				: this.getMethodClass().getClassName()) + "." + methodName);
		if (!belongInterface) {
			System.out.println("方法体：" + bodyStr);
			System.out.println("起止位置: " + bodyBegin + "-" + bodyEnd);
			System.out.println("方法属性：" + (isPrivateMethod ? "私有方法" : "公共方法"));
		}
		System.out.println("入参个数：" + paramCount);
		System.out.println("是否为接口方法：" + (belongInterface ? "是" : "否"));
		if (StringUtils.isNotBlank(serviceId)) {
			System.out.println("对应原子功能号: " + serviceId);
		}

		if (StringUtils.isNotBlank(url)) {
			System.out.println("对应Url请求：" + url);
		}
		if (getFunctionSetAll() != null && !getFunctionSetAll().isEmpty()) {
			System.out.println("方法" + (belongInterface ? this.getMethodInterface().getInterfaceName()
					: this.getMethodClass().getClassName()) + "." + methodName + "调用的功能号有：");
			for (String temp : getFunctionSetAll()) {
				System.out.print(temp);
				System.out.print("、");
			}
			System.out.println();
		}
		if (this.getInvokeMethods() != null && !this.getInvokeMethods().isEmpty()) {
			System.out.println("方法" + (belongInterface ? this.getMethodInterface().getInterfaceName()
					: this.getMethodClass().getClassName()) + "." + methodName + "中调用了以下方法：");
			for (ServiceImpleMethod temp : this.getInvokeMethods()) {
				System.out.println("被调用方法名：" + temp.getMethodName());
				System.out.println("被调用方法入参个数：" + temp.getParamCount());
				// System.out.println("被调用方法对应实现类：" +
				// temp.getServiceImpl().getName());
			}
		}
		System.out.println("---方法：" + this.getMethodName() + "分析结束------");
		System.out.println();
		return null;
	}

	public void printfSelf(Map<String, BopClass> bopActionMap) {
		System.out.println("---接口方法：" + this.getMethodName() + "分析开始------");
		System.out.println("方法名：" + (belongInterface ? this.getMethodInterface().getInterfaceName()
				: this.getMethodClass().getClassName()) + "." + methodName);
		// if (!belongInterface) {
		// System.out.println("方法体：" + bodyStr);
		// System.out.println("起止位置: " + bodyBegin + "-" + bodyEnd);
		// System.out.println("方法属性：" + (isPrivateMethod ? "私有方法" : "公共方法"));
		// }
		System.out.println("入参个数：" + paramCount);
		System.out.println("是否为接口方法：" + (belongInterface ? "是" : "否"));
		if (StringUtils.isNotBlank(serviceId)) {
			System.out.println("对应原子功能号: " + serviceId);
		}

		if (StringUtils.isNotBlank(url)) {
			System.out.println("对应Url请求：" + url);
		}
		if (getFunctionSetAll() != null && !getFunctionSetAll().isEmpty()) {
			System.out.println("方法" + (belongInterface ? this.getMethodInterface().getInterfaceName()
					: this.getMethodClass().getClassName()) + "." + methodName + "调用的功能号有：");
			for (String temp : getFunctionSetAll()) {
				System.out.print(temp);
				System.out.print("、");
			}
			System.out.println();
		}
		if (this.getInvokeMethods() != null && !this.getInvokeMethods().isEmpty()) {
			System.out.println("方法" + (belongInterface ? this.getMethodInterface().getInterfaceName()
					: this.getMethodClass().getClassName()) + "." + methodName + "中调用了以下方法：");
			for (ServiceImpleMethod temp : this.getInvokeMethods()) {
				System.out.println("被调用方法名：" + temp.getMethodName());
				System.out.println("被调用方法入参个数：" + temp.getParamCount());
				// System.out.println("被调用方法对应实现类：" +
			}
		}
		if (this.getMethodInterface().getInterfaceClass().isInterface()
				&& this.getMethodInterface().getServiceImpl() != null) {
			BopClass classTemp = bopActionMap.get(this.getMethodInterface().getServiceImpl().getTypeName());
			if (classTemp != null) {
				BopMethod methodTemp = classTemp.getMethodByMethod(this.method);
				if (methodTemp != null) {
					if (methodTemp.getFunctionSetAll() != null && !methodTemp.getFunctionSetAll().isEmpty()) {
						System.out
								.println("方法"
										+ (belongInterface ? this.getMethodInterface().getInterfaceName()
												: this.getMethodClass().getClassName())
										+ "." + methodName + "调用的功能号有：");
						for (String temp : methodTemp.getFunctionSetAll()) {
							System.out.print(temp);
							System.out.print("、");
						}
						System.out.println();
					}
				} else {
					if (StringUtils.indexOf("IBopCacheService,ApiConfig",
							this.getMethodInterface().getInterfaceName()) < 0) {
						System.out.println("出现异常！");
						System.out.println("方法" + method.getName() + "未找到实现方法");
					}
				}
			} else {
				System.out.println("出现异常！");
				System.out.println(this.getMethodInterface().getServiceImpl().getTypeName() + "实现类未找到！");
			}
		} else {
			if (StringUtils.indexOf(
					"ISrvOpenBankAcctService,Figure,Date,CheckValue,IShineEbicsService,QuerySystemUserCache",
					this.getMethodInterface().getInterfaceName()) < 0) {
				System.out.println("出现异常！");
				System.out.println(this.getMethodInterface().getInterfaceName() + ".未找到实现类");
			}
		}

		System.out.println("---接口方法：" + this.getMethodName() + "分析结束------");
		System.out.println();
	}

}
