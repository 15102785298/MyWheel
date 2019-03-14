package com.tantian.bopMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.swing.plaf.synth.SynthSpinnerUI;
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

	public static void main(String[] args) throws IOException {

		String proJectPath = "D:/aaWork/WEBcode/bop-web-20190311/bop2.0";
		System.out.println("请输入代码路径：");
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String customPath = bf.readLine();
		proJectPath = StringUtils.isBlank(customPath) ? proJectPath : customPath;
		System.out.println("请输入希望保存的分析文件路径：");
		customPath = bf.readLine();
		String savePath = "D:/";
		savePath = StringUtils.isBlank(customPath) ? savePath : customPath;
		System.out.println("请选择匹配模式（0:快速模式，更快但可能存在不准确信息（默认），1：完全模式，较慢但精确度高）：");
		customPath = bf.readLine();
		boolean fastModel = true;
		if (StringUtils.equals(customPath, "1")) {
			fastModel = false;
		}
		savePath += "/BOP引用的功能号" + (fastModel ? "快速模式" : "完全模式") + "版本[" + System.currentTimeMillis() + "].txt";
		System.out.println("请选择分析用的线程数量（建议为物理线程一半，默认单线程）");
		customPath = bf.readLine();
		int maxThread = 1;
		try {
			maxThread = Integer.parseInt(customPath);
		} catch (Exception e) {
			maxThread = 1;
		}
		System.out.println("分析开始......");
		System.out.println("代码源路径【" + proJectPath + "】");
		System.out.println("结果导出路径【" + savePath + "】");
		System.out.println("导出模式【" + (fastModel ? "快速模式" : "完全模式") + "】");
		System.out.println("使用线程数量【" + maxThread + "】个");
		System.out.println("加载数据源......");
		/*
		 * 开始读取vm文件信息
		 */
		// 代码路径

		List<File> vmFileList = FileUtils.getAllFile(proJectPath, ".vm");
		List<File> javaFileList = FileUtils.getAllFile(proJectPath, ".java");
		// javaFileList.addAll(FileUtils.getAllFile("D:/aaWork/WEBcode/bop-web-20180305/bop-platform",
		// ".java"));
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

		BopService2functionUtils.getService2FunctionMap(javaFileList, uffunctionFileList, savePath, fastModel,
				maxThread);

		/*
		 * // 获取functionName列表 List<String> functionName = new ArrayList<>();
		 * List<String> function = new ArrayList<>();
		 *
		 * functionName.add("CNST_FUNCID_CUST_FUNDACCOUNT_GET");
		 * functionName.add("LS_ACCTFORBOP_CLIENT_QRY");
		 * functionName.add("LS_ACCTFORBOP_FUNDACCT_BASEINFO_GET");
		 * functionName.add("CNST_FUNCID_SVRASSET_FUNDACCOUNT_QRY");
		 * functionName.add("CNST_FUNCID_CUST_OTHER_GET");
		 * functionName.add("LS_ACCTFORBOP_ORGAN_INFO_GET");
		 *
		 * function.add("143014"); function.add("170002");
		 * function.add("170003"); function.add("321001");
		 * function.add("143012"); function.add("170001"); for (File temp :
		 * uffunctionFileList) {
		 * functionName.addAll(BopService2functionUtils.getFunctionNameValue(
		 * temp)); // 获取function列表
		 * function.addAll(BopService2functionUtils.getFunctionValue(temp)); }
		 *
		 * int left = vmFileList.size(); List<File> UrlValuesList =
		 * FileUtils.getAllFile(proJectPath, "UrlValues.java"); // 输入vm文件信息 for
		 * (File temp : vmFileList) { bopVmList.add(new BopVm(temp, proJectPath,
		 * UrlValuesList)); System.out.println("读取文件：" + temp.getName() + "剩余："
		 * + --left); }
		 *
		 * // 开始读取java文件信息（加载类） // 初始化类文件Map for (File temp : javaFileList) {
		 * String className = StringUtils.replace(
		 * "com\\hundsun\\" + StringUtils.substringAfterLast(temp.getPath(), "
		 * com\\hundsun\\"), "\\", ".");
		 * javaFileMap.put(StringUtils.substring(className, 0,
		 * className.length() - 5), temp); } BopClassLoaderUtils classLoader =
		 * new BopClassLoaderUtils(FileUtils.getAllFile("D:\\aaWork\\WEB-INF",
		 * ".jar")); Map<String, Class<?>> allClass = classLoader.getAllClass();
		 * int leftfinal = allClass.size(); for (Entry<String, Class<?>> temp :
		 * allClass.entrySet()) { Class<?> tempClass = temp.getValue(); File
		 * javaFile = javaFileMap.get(tempClass.getName()); if (javaFile ==
		 * null) { continue; } if (tempClass.isInterface()) {
		 * bopInterfaceMap.put(tempClass.getName(), new BopInterface(javaFile,
		 * tempClass)); } else { bopActionMap.put(tempClass.getName(), new
		 * BopClass(javaFile, tempClass, allClass, functionName, function)); }
		 * System.out.println("剩余" + --leftfinal); } //
		 * bopActionMap.putAll(bopClassMap); System.out.println();
		 * System.out.println(); System.out.println(); System.out.println();
		 *
		 * String ignoreInterface =
		 * "com.hundsun.jres.workflow.remoting.service.DefinitionService,接口.com.hundsun.jres.workflow.remoting.service.FormService.未找到实现类接口.com.hundsun.jres.workflow.remoting.service.InstanceService.未找到实现类接口.com.hundsun.jres.workflow.remoting.service.TaskService.未找到实现类com.hundsun.jres.workflow.remoting.service.TaskService,com.hundsun.jres.workflow.remoting.service.InstanceService,com.hundsun.jres.workflow.remoting.service.FormService,com.hundsun.jres.workflow.remoting.service.InstanceService,com.hundsun.jres.workflow.remoting.service.TaskService,com.hundsun.jres.workflow.remoting.service.FormService,com.hundsun.user.biz.impl.cache.CacheManagerImpl,com.hundsun.jresplus.base.cache.CacheManagerImpl,com.hundsun.user.util.SysConfigUtil";
		 * String ignoreLoad = ignoreInterface +
		 * "com.hundsun.jresplus.beans.ObjectFactoryImpl,类.com.hundsun.user.service.sysarg.LicenseManageServiceImpl.未加载.com.hundsun.user.biz.impl.login.LoginManagerImpl.未加载com.hundsun.jresplus.web.url.URLBroker,com.hundsun.jresplus.middleware.MiddlewareServiceImpl,com.hundsun.jresplus.web.servlet.MediaTypesHandler,com.hundsun.jresplus.middleware.MiddlewareServiceImpl,com.hundsun.jresplus.base.dict.DictManagerImpl";
		 *
		 * // 将方法注入类的功能号加入setAll for (Entry<String, BopClass> temp :
		 * bopActionMap.entrySet()) { BopClass tempClass = temp.getValue(); for
		 * (BopMethod tempMethod1 : tempClass.getBopSelfMethods()) { for
		 * (ServiceImpleMethod tempInvokedClaa : tempMethod1.getInvokeMethods())
		 * { Class<?> implClass = tempInvokedClaa.getServiceImpl(); if
		 * (implClass != null) { BopClass theClass =
		 * bopActionMap.get(implClass.getName()); if (theClass != null) {
		 * BopMethod theMethod =
		 * theClass.getMethodByMethodNameAndParam(tempInvokedClaa); if
		 * (theMethod != null) {
		 * tempMethod1.functionSetAllAdd(theMethod.getFunctionSetAll()); } else
		 * { System.out.println("类." + implClass.getName() + ".未找到方法." +
		 * tempInvokedClaa.getMethodName() + tempInvokedClaa.getParamCount() +
		 * "调用位置." + tempMethod1.getMethodClass().getClassName() + "." +
		 * tempMethod1.getMethodName()); } } else { if
		 * (ignoreLoad.indexOf(implClass.getName()) < 0) {
		 * System.out.println("类." + implClass.getName() + ".未加载"); } } } else {
		 * if (implClass == null) { System.out.println("接口." +
		 * tempInvokedClaa.getServiceName() + ".未找到实现类"); continue; } } } } }
		 *
		 * for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
		 * temp.getValue().printfSelf(); }
		 *
		 * Set<String> errorUrl = new HashSet<>(); // 完成类加载,开始找对应关系 for (BopVm
		 * vm : bopVmList) { System.out.println("界面" + vm.getVmsName()); for
		 * (BopJson json : vm.getUrlJson()) { BopMethod method =
		 * findTheMethodByJson(json, bopActionMap); if (method == null) {
		 * errorUrl.add(json.getUrlJson()); } else { System.out.println("请求." +
		 * json.getUrlJson() + ".对应功能号："); for (String methodRef :
		 * method.getFunctionSetAll()) { System.out.println(methodRef); } } } }
		 * System.out.println("异常请求："); for (String temp : errorUrl) {
		 * System.out.println(temp); }
		 */

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
