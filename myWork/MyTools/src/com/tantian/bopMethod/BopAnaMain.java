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
		System.out.println("���������·����");
		BufferedReader bf = new BufferedReader(new InputStreamReader(System.in));
		String customPath = bf.readLine();
		proJectPath = StringUtils.isBlank(customPath) ? proJectPath : customPath;
		System.out.println("������ϣ������ķ����ļ�·����");
		customPath = bf.readLine();
		String savePath = "D:/";
		savePath = StringUtils.isBlank(customPath) ? savePath : customPath;
		System.out.println("��ѡ��ƥ��ģʽ��0:����ģʽ�����쵫���ܴ��ڲ�׼ȷ��Ϣ��Ĭ�ϣ���1����ȫģʽ����������ȷ�ȸߣ���");
		customPath = bf.readLine();
		boolean fastModel = true;
		if (StringUtils.equals(customPath, "1")) {
			fastModel = false;
		}
		savePath += "/BOP���õĹ��ܺ�" + (fastModel ? "����ģʽ" : "��ȫģʽ") + "�汾[" + System.currentTimeMillis() + "].txt";
		System.out.println("��ѡ������õ��߳�����������Ϊ�����߳�һ�룬Ĭ�ϵ��̣߳�");
		customPath = bf.readLine();
		int maxThread = 1;
		try {
			maxThread = Integer.parseInt(customPath);
		} catch (Exception e) {
			maxThread = 1;
		}
		System.out.println("������ʼ......");
		System.out.println("����Դ·����" + proJectPath + "��");
		System.out.println("�������·����" + savePath + "��");
		System.out.println("����ģʽ��" + (fastModel ? "����ģʽ" : "��ȫģʽ") + "��");
		System.out.println("ʹ���߳�������" + maxThread + "����");
		System.out.println("��������Դ......");
		/*
		 * ��ʼ��ȡvm�ļ���Ϣ
		 */
		// ����·��

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
		 * // ��ȡfunctionName�б� List<String> functionName = new ArrayList<>();
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
		 * temp)); // ��ȡfunction�б�
		 * function.addAll(BopService2functionUtils.getFunctionValue(temp)); }
		 *
		 * int left = vmFileList.size(); List<File> UrlValuesList =
		 * FileUtils.getAllFile(proJectPath, "UrlValues.java"); // ����vm�ļ���Ϣ for
		 * (File temp : vmFileList) { bopVmList.add(new BopVm(temp, proJectPath,
		 * UrlValuesList)); System.out.println("��ȡ�ļ���" + temp.getName() + "ʣ�ࣺ"
		 * + --left); }
		 *
		 * // ��ʼ��ȡjava�ļ���Ϣ�������ࣩ // ��ʼ�����ļ�Map for (File temp : javaFileList) {
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
		 * System.out.println("ʣ��" + --leftfinal); } //
		 * bopActionMap.putAll(bopClassMap); System.out.println();
		 * System.out.println(); System.out.println(); System.out.println();
		 *
		 * String ignoreInterface =
		 * "com.hundsun.jres.workflow.remoting.service.DefinitionService,�ӿ�.com.hundsun.jres.workflow.remoting.service.FormService.δ�ҵ�ʵ����ӿ�.com.hundsun.jres.workflow.remoting.service.InstanceService.δ�ҵ�ʵ����ӿ�.com.hundsun.jres.workflow.remoting.service.TaskService.δ�ҵ�ʵ����com.hundsun.jres.workflow.remoting.service.TaskService,com.hundsun.jres.workflow.remoting.service.InstanceService,com.hundsun.jres.workflow.remoting.service.FormService,com.hundsun.jres.workflow.remoting.service.InstanceService,com.hundsun.jres.workflow.remoting.service.TaskService,com.hundsun.jres.workflow.remoting.service.FormService,com.hundsun.user.biz.impl.cache.CacheManagerImpl,com.hundsun.jresplus.base.cache.CacheManagerImpl,com.hundsun.user.util.SysConfigUtil";
		 * String ignoreLoad = ignoreInterface +
		 * "com.hundsun.jresplus.beans.ObjectFactoryImpl,��.com.hundsun.user.service.sysarg.LicenseManageServiceImpl.δ����.com.hundsun.user.biz.impl.login.LoginManagerImpl.δ����com.hundsun.jresplus.web.url.URLBroker,com.hundsun.jresplus.middleware.MiddlewareServiceImpl,com.hundsun.jresplus.web.servlet.MediaTypesHandler,com.hundsun.jresplus.middleware.MiddlewareServiceImpl,com.hundsun.jresplus.base.dict.DictManagerImpl";
		 *
		 * // ������ע����Ĺ��ܺż���setAll for (Entry<String, BopClass> temp :
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
		 * { System.out.println("��." + implClass.getName() + ".δ�ҵ�����." +
		 * tempInvokedClaa.getMethodName() + tempInvokedClaa.getParamCount() +
		 * "����λ��." + tempMethod1.getMethodClass().getClassName() + "." +
		 * tempMethod1.getMethodName()); } } else { if
		 * (ignoreLoad.indexOf(implClass.getName()) < 0) {
		 * System.out.println("��." + implClass.getName() + ".δ����"); } } } else {
		 * if (implClass == null) { System.out.println("�ӿ�." +
		 * tempInvokedClaa.getServiceName() + ".δ�ҵ�ʵ����"); continue; } } } } }
		 *
		 * for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
		 * temp.getValue().printfSelf(); }
		 *
		 * Set<String> errorUrl = new HashSet<>(); // ��������,��ʼ�Ҷ�Ӧ��ϵ for (BopVm
		 * vm : bopVmList) { System.out.println("����" + vm.getVmsName()); for
		 * (BopJson json : vm.getUrlJson()) { BopMethod method =
		 * findTheMethodByJson(json, bopActionMap); if (method == null) {
		 * errorUrl.add(json.getUrlJson()); } else { System.out.println("����." +
		 * json.getUrlJson() + ".��Ӧ���ܺţ�"); for (String methodRef :
		 * method.getFunctionSetAll()) { System.out.println(methodRef); } } } }
		 * System.out.println("�쳣����"); for (String temp : errorUrl) {
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
