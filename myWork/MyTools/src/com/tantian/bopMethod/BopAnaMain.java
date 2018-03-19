package com.tantian.bopMethod;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.text.html.parser.Entity;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopModel.BopClass;
import com.tantian.bopModel.BopInterface;
import com.tantian.bopModel.BopJson;
import com.tantian.bopModel.BopMethod;
import com.tantian.bopModel.BopVm;
import com.tantian.bopModel.ServiceImpleMethod;
import com.tantian.bopUtils.BopClassLoaderUtils;
import com.tantian.bopUtils.BopService2functionUtils;
import com.tantian.bopUtils.FileUtils;

public class BopAnaMain {

	public static void main(String[] args) {
		System.out.println("分析开始......");
		/*
		 * 开始读取vm文件信息
		 */
		String proJectPath = "D:/aaWork/WEBcode/bop-web-20180305/bop2.0";
		List<File> vmFileList = FileUtils.getAllFile(proJectPath, ".vm");
		List<File> javaFileList = FileUtils.getAllFile(proJectPath, ".java");
		javaFileList.addAll(FileUtils.getAllFile("D:/aaWork/WEBcode/bop-web-20180305/bop-platform", ".java"));
		List<File> uffunctionFileList = FileUtils.getAllFile(
				proJectPath + "/bop-biz-platform/bop-pub/src/main/java/com/hundsun/bop/pub/constant/UFFunction.java",
				".java");
		uffunctionFileList.addAll(FileUtils.getAllFile(
				proJectPath + "/bop-biz-platform/bop-pub/src/main/java/com/hundsun/bop/pub/constant/Function.java",
				".java"));
		uffunctionFileList.addAll(FileUtils.getAllFile(
				proJectPath + "\\bop-ext\\bop-ext-pub\\src\\main\\java\\com\\hundsun\\bop\\ext\\biz\\pub\\function",
				".java"));
		Map<String, File> javaFileMap = new HashMap<>();

		List<BopVm> bopVmList = new LinkedList<>();
		// Map<String, BopVm> bopVmMap = new HashMap<>();
		Map<String, BopInterface> bopInterfaceMap = new HashMap<>();
		Map<String, BopClass> bopClassMap = new HashMap<>();
		Map<String, BopClass> bopActionMap = new HashMap<>();

		// bopService2Function =
		// BopService2functionUtils.getService2FunctionMap(javaFileList,
		// uffunctionFileList);
		// 获取functionName列表
		List<String> functionName = new ArrayList<>();
		List<String> function = new ArrayList<>();
		for (File temp : uffunctionFileList) {
			functionName.addAll(BopService2functionUtils.getFunctionNameValue(temp));
			// 获取function列表
			function.addAll(BopService2functionUtils.getFunctionValue(temp));
		}

		int left = vmFileList.size();
		List<File> UrlValuesList = FileUtils.getAllFile(proJectPath, "UrlValues.java");
		// 输入vm文件信息
		for (File temp : vmFileList) {
			bopVmList.add(new BopVm(temp, proJectPath, UrlValuesList));
			System.out.println("读取文件：" + temp.getName() + "剩余：" + --left);
		}

		// 开始读取java文件信息（加载类）
		// 初始化类文件Map
		for (File temp : javaFileList) {
			String className = StringUtils.replace(
					"com\\hundsun\\" + StringUtils.substringAfterLast(temp.getPath(), "com\\hundsun\\"), "\\", ".");
			javaFileMap.put(StringUtils.substring(className, 0, className.length() - 5), temp);
		}
		BopClassLoaderUtils classLoader = new BopClassLoaderUtils(FileUtils.getAllFile("D:\\aaWork\\WEB-INF", ".jar"));
		Map<String, Class<?>> allClass = classLoader.getAllClass();
		int leftfinal = allClass.size();
		for (Entry<String, Class<?>> temp : allClass.entrySet()) {
			Class<?> tempClass = temp.getValue();
			File javaFile = javaFileMap.get(tempClass.getName());
			if (javaFile == null) {
				continue;
			}
			if (tempClass.isInterface()) {
				bopInterfaceMap.put(tempClass.getName(), new BopInterface(javaFile, tempClass));
			} else {
				bopActionMap.put(tempClass.getName(),
						new BopClass(javaFile, tempClass, allClass, functionName, function));
			}
			System.out.println("剩余" + --leftfinal);
		}
		// bopActionMap.putAll(bopClassMap);
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		// 将方法注入类的功能号加入setAll
		for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
			BopClass tempClass = temp.getValue();
			for (BopMethod tempMethod1 : tempClass.getBopSelfMethods()) {
				for (ServiceImpleMethod tempInvokedClaa : tempMethod1.getInvokeMethods()) {
					Class<?> implClass = tempInvokedClaa.getServiceImpl();
					if (implClass != null
							&& "com.hundsun.user.biz.impl.cache.CacheManagerImpl,".contains(implClass.getName())) {
						BopClass theClass = bopActionMap.get(implClass.getName());
						if (theClass != null) {
							BopMethod theMethod = theClass.getMethodByMethodName(tempInvokedClaa.getMethodName());
							if (theMethod != null) {
								tempMethod1.functionSetAllAdd(theMethod.getFunctionSetAll());
							} else {
								System.out.println(
										"类." + implClass.getName() + ".未找到方法." + tempInvokedClaa.getMethodName());
							}
						} else {
							System.out.println("类." + implClass.getName() + ".未加载");
						}
					} else {
						System.out.println("接口." + tempInvokedClaa.getServiceName() + ".未找到实现类");
					}
				}
			}
		}

		for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
			temp.getValue().printfSelf();
		}

		// 完成类加载,开始找对应关系
		for (BopVm vm : bopVmList) {
			System.out.println("界面" + vm.getVmsName());
			for (BopJson json : vm.getUrlJson()) {
				BopMethod method = findTheMethodByJson(json, bopActionMap);
				if (method == null) {
					System.out.println(json.getUrlJson());
					System.out.println("获取请求出现异常!");
				} else {
					System.out.println("请求." + json.getUrlJson() + ".对应功能号：");
					for (String methodRef : method.getFunctionSetAll()) {
						System.out.println(methodRef);
					}
				}
			}
		}

	}

	private static BopMethod findTheMethodByJson(BopJson json, Map<String, BopClass> bopActionMap) {
		for (Entry<String, BopClass> tempEntry : bopActionMap.entrySet()) {
			for (BopMethod tempMethod : tempEntry.getValue().getBopSelfMethods()) {
				if (StringUtils.endsWith(tempMethod.getUrl(), json.getUrlJson())) {
					return tempMethod;
				}
			}
		}
		return null;
	}

	private static boolean isService(Class<?> tempClass) {
		for (Annotation temp : tempClass.getAnnotations()) {
			if (StringUtils.endsWith(temp.annotationType().getTypeName(), "org.springframework.stereotype.Service")) {
				return true;
			}
			if (StringUtils.endsWith(temp.annotationType().getTypeName(), "Component")) {
				return true;
			}
		}
		return false;
	}

	private static boolean isAction(Class<?> tempClass) {
		for (Annotation temp : tempClass.getAnnotations()) {
			if (StringUtils.endsWith(temp.annotationType().getTypeName(),
					"org.springframework.stereotype.Controller")) {
				return true;
			}
		}
		return false;
	}

}
