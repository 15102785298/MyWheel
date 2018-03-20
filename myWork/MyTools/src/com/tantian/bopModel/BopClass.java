package com.tantian.bopModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

/**
 * 类
 *
 * @author hspcadmin
 *
 */
public class BopClass {
	// 类名
	private String className;
	// 路径
	private String classPath;
	// 文件
	private File classFile;
	private Class classClass;
	// 类自己的方法
	private List<BopMethod> bopSelfMethods;
	// 类实现的接口
	private List<BopInterface> bopImplementsInterface;
	// 类继承的类
	private List<BopClass> bopImplementsClass;
	// 类引用的Bean
	private List<BopClass> bopQuoteClass;
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

	public String getFileContant() {
		return fileContant;
	}

	public void setFileContant(String fileContant) {
		this.fileContant = fileContant;
	}

	public BopMethod getMethodByMethodName(String methodName) {
		for (BopMethod temp : this.getBopSelfMethods()) {
			if (temp.getMethodName().equals(methodName)) {
				return temp;
			}
		}
		return null;
	}

	public BopClass(File javaFile, Class<?> tempClass, Map<String, Class<?>> allClasses, List<String> functionName,
			List<String> function) {
		this.classFile = javaFile;
		this.className = StringUtils.split(classFile.getName(), ".")[0];
		this.classPath = classFile.getAbsolutePath();
		this.classClass = tempClass;
		try {
			this.fileContant = reader(javaFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.bopSelfMethods = getAllSelfMethod(classClass, classFile, allClasses, functionName, function);
	}

	public void printfSelf() {
		System.out.println("类名称：" + className);
		System.out.println("类路径：" + classPath);
//		for (BopMethod temp : bopSelfMethods) {
//			System.out.println(className + "." + temp.getMethodName());
//			for (String tempString : temp.getFunctionSetAll()) {
//				System.out.println(tempString);
//			}
//		}
		if (!bopSelfMethods.isEmpty()) {
			List<BopMethod> errorList = new LinkedList<>();
			for (BopMethod temp : bopSelfMethods) {
				BopMethod errorClass = temp.printfSelf();
				if (errorClass != null) {
					if (StringUtils.startsWith(errorClass.getMethodClass().getClassClass().getName(), "com.hundsun.bop")
							&& !StringUtils.startsWith(errorClass.getMethodName(), "access")) {
						errorList.add(errorClass);
					}
				}
			}
			if (!errorList.isEmpty()) {
				System.out.println("className.存在出错方法------------------------------------------");
				for (BopMethod temp : errorList) {
					System.out.println(temp.getMethodClass().className + "." + temp.getMethodName());
				}
				System.out.println(this.getFileContant());
			}
		}
		System.out.println();
		System.out.println();
		System.out.println();

	}

	/**
	 * 获取类中的方法
	 *
	 * @param interfaceClass
	 * @param interfaceFile2
	 * @param bopService2Function
	 * @return
	 */
	private List<BopMethod> getAllSelfMethod(Class<?> interfaceClass, File interfaceFile2,
			Map<String, Class<?>> allClasses, List<String> functionName, List<String> function) {
		List<BopMethod> res = new ArrayList<>();
		for (Method temp : interfaceClass.getDeclaredMethods()) {
			if (!StringUtils.startsWith(temp.getName(), "access$")) {
				try {
					res.add(new BopMethod(this, "", temp, res, allClasses, functionName, function));
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		int length = res.size();
		for (int i = 0; i < length; i++) {
			BopMethod temp = res.get(i);
			for (Integer refItem : temp.getLocalRefList()) {
				BopMethod aimMethod = getAimMethod(res, temp, refItem.intValue());
				if (aimMethod != null) {
					aimMethod.addRefItem(temp);
					aimMethod.functionSetAllAdd(temp.getFunctionSet());
					System.out.println(this.getClassName() + "." + temp.getMethodName() + ".匹配成功-----------");
					System.out.println(temp.getMethodName() + "." + refItem);
					System.out.println(
							aimMethod.getMethodName() + "." + aimMethod.getBodyBegin() + "-" + aimMethod.getBodyEnd());
					System.out.println();
				} else {
					System.out.println(temp.getMethodName() + ".匹配失败-----------");
					System.out.println(refItem);
					for (BopMethod tt : res) {
						System.out.println(tt.getMethodName() + ":" + tt.getBodyBegin() + "=" + tt.getBodyEnd());
					}
					System.out.println(this.getFileContant());
					System.out.println(temp.getMethodName() + ".匹配失败-----------");
				}
			}
		}

		for (int i = 0; i < length; i++) {
			BopMethod temp = res.get(i);
			System.out.println("分析方法调用树." + this.getClassName() + "." + temp.getMethodName());
			System.out.println(temp.getBodyStr());
			addMethod(temp, temp, 0);
		}
		for (BopMethod temp : res) {
			for (BopMethod temp2 : temp.getRefListAll()) {
				temp.addInvokeMethods(temp2);
				// 将类中方法自己调用的功能号合并
				temp.functionSetAllAdd(temp2.getFunctionSet());
			}
			temp.functionSetAllAdd(temp.getFunctionSet());

			if (!temp.getInvokeMethods().isEmpty()) {
				// System.out.println(temp.getMethodName() +
				// ".service调用开始------------------");
				// for (Class<?> temp2 : temp.getInvokeMethods()) {
				// System.out.println(
				// temp2.getSimpleName() + "-->" + temp2.getSimpleName() + "." +
				// temp2.getSimpleName());
				// }
				// System.out.println(temp.getMethodName() +
				// ".service调用结束------------------");
			}
		}
		return res;
	}

	private BopMethod getAimMethod(List<BopMethod> allMethod, BopMethod nowMethod, Integer index) {
		for (BopMethod method : allMethod) {
			if (method.getBodyBegin() - 2 <= index && index <= method.getBodyBegin() + 2) {
				continue;
			}
			if (method.isInTheMethod(index)) {
				return method;
			}
		}
		return null;

	}

	private Set<BopMethod> addMethod(BopMethod a, BopMethod b, int time) {
		if (time > 5) {
			return null;
		}
		if (a.isRefListEmpty()) {
			return null;
		}
		for (BopMethod temp : a.getRefList()) {
			a.addRefListAll(addMethod(temp, b, ++time));
		}
		a.addRefListAll(a.getRefList());
		// System.out.println(a.getMethodName() + ".调用分析完毕--------");
		// if (a.getRefList().size() != a.getRefListAll().size()) {
		// System.out.println(a.getRefList().size() + "-->" +
		// a.getRefListAll().size());
		// }
		// for (BopMethod temp : a.getRefListAll()) {
		// System.out.println(a.getMethodName() + ".调用了." +
		// temp.getMethodName());
		// for (BopMethod temp2 : temp.getRefList()) {
		// System.out.println(temp2.getMethodName() + "-内部-" +
		// temp2.getRefList().size());
		// }
		// System.out.println(temp.getMethodName() + "--" +
		// temp.getRefList().size());
		// }
		// System.out.println(a.getMethodName() + ".调用分析完毕--------");

		return a.getRefListAll();

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

	public BopMethod getMethodByMethodNameAndParam(ServiceImpleMethod tempInvokedClaa) {
		boolean isDt = false;
		String methodName = tempInvokedClaa.getMethodName();
		int methodParam = tempInvokedClaa.getParamCount();
		for (BopMethod temp : this.getBopSelfMethods()) {
			if (temp.getMethodName().equals(methodName)) {
				if (temp.getParamCount() == methodParam) {
					return temp;
				} else {
					isDt = true;
				}
			}
		}
		if (isDt) {
			System.out.println("多态匹配失败" + methodName + methodParam + ".调用位置." + tempInvokedClaa);
		}
		return null;
	}
}
