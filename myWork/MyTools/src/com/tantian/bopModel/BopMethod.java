package com.tantian.bopModel;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
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
	private String inParamStr = "";
	private Set<Integer> localRefList = null;
	private Set<BopMethod> beRefList = new HashSet<>();
	private Set<BopMethod> refList = new HashSet<>();
	private Set<BopMethod> refListAll = new HashSet<>();

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

	public Set<Integer> getLocalRefList() {
		return localRefList;
	}

	public void setLocalRefList(Set<Integer> localRefList) {
		this.localRefList = localRefList;
	}

	public BopMethod(BopClass methodClass, String methodBody, Method method, List<BopMethod> nowMethodList) {
		this.belongInterface = false;
		this.methodClass = methodClass;
		this.methodName = method.getName();
		this.bopInParams = method.getParameters();
		this.method = method;
		this.isPrivateMethod = StringUtils.contains(Modifier.toString(method.getModifiers()), "private");
		String nowFileString = belongInterface ? methodInterface.getFileContant() : methodClass.getFileContant();
		String anaString = nowFileString;
		this.bodyStr = getBodyString(anaString);
		this.invokeMethods = getAllIncokeMethods();
		this.localRefList = calcuteLocalRefList();
		while ((!analize())) {

			anaString = StringUtils.substring(anaString, anaString.indexOf(this.bodyStr) + methodName.length());
			this.bodyStr = getBodyString(anaString);
			this.invokeMethods = getAllIncokeMethods();
			this.localRefList = calcuteLocalRefList();
		}
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
		if(this.getMethodName().equals("delProduct")){
			System.out.println();
		}
		this.bodyBegin = StringUtils.indexOf(this.getMethodClass().getFileContant(), " " + this.bodyStr);
		if(this.bodyBegin == -1){
			this.bodyBegin = StringUtils.indexOf(this.getMethodClass().getFileContant(), ">" + this.bodyStr);
		}
		this.bodyEnd = this.bodyBegin + bodyStr.length();
		Set<Integer> res = new HashSet<>();
		Pattern	pattern = Pattern.compile("([^\\.]\\s)+(\\b)" + methodName + "(\\s*\\(|\\()");
		Matcher macher = pattern.matcher(methodClass.getFileContant());

		while (macher.find()) {
			int index = macher.start() + 1;
			if (bodyBegin > index  || index > bodyEnd) {
				res.add(index);
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
		String nowFileString = belongInterface ? methodInterface.getFileContant() : methodClass.getFileContant();
		// this.bodyStr = getBodyString(nowFileString);
		this.invokeMethods = getAllIncokeMethods();
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
		macher.find();
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
		// System.out.println("--------------方法." + methodName +
		// "属性------------------------");
		System.out.println("方法." + methodName + ".起始位置: " + bodyBegin + "-" + bodyEnd);
		System.out.println(bodyStr);
		// System.out.println("是否属于接口：" + (belongInterface ? "是" : "否"));
		// if (!belongInterface && !(methodName.startsWith("get") ||
		// methodName.startsWith("set"))
		// && (bodyBegin == -1 || bodyEnd == -1)) {
		// return this;
		//
		// }
		// System.out.println("是否是私有方法：" + (isPrivateMethod ? "是" : "否"));
		// if (!this.invokeMethods.isEmpty()) {
		// System.out.println("---------------方法体" + methodName +
		// "中调用的方法----------");
		// for (ServiceImpleMethod temp : this.invokeMethods) {
		// temp.printfSelf();
		// }
		// System.out.println("---------------方法体" + methodName +
		// "中调用的方法----------");
		// }
		if (getLocalRefList() != null && !getLocalRefList().isEmpty()) {
			System.out.println("方法." + methodName + ".出现位置--------------");
			for (Integer temp : getLocalRefList()) {
				System.out.println(temp);
			}
		}

		return null;
	}

}
