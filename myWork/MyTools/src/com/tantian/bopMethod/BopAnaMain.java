package com.tantian.bopMethod;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
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

	static int stackNum = 8;

	public static void main(String[] args) {
		long t1 = System.currentTimeMillis();
		System.out.println("������ʼ......");
		/*
		 * ��ʼ��ȡvm�ļ���Ϣ
		 */
		String proJectPath = "D:\\aaWork\\WEBcode\\bop-web-20180305\\bop2.0";
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
		// ��ȡfunctionName�б�
		List<String> functionName = new ArrayList<>();
		List<String> function = new ArrayList<>();

		// functionName.add("LS_ACCTFORBOP_CLIENT_QRY");
		// functionName.add("CNST_FUNCID_SVRASSET_FUNDACCOUNT_QRY");
		// functionName.add("CNST_FUNCID_CUST_OTHER_GET");
		// functionName.add("CNST_FUNCID_FUNDACCT_INFO_GET");
		//
		// function.add("170002");
		// function.add("321001");
		// function.add("143012");
		// function.add("144032");
		for (File temp : uffunctionFileList) {
			functionName.addAll(BopService2functionUtils.getFunctionNameValue(temp));
			// ��ȡfunction�б�
			function.addAll(BopService2functionUtils.getFunctionValue(temp));
		}

		int left = vmFileList.size();
		List<File> UrlValuesList = FileUtils.getAllFile(proJectPath, "UrlValues.java");
		Set<String> jsonSet = getJsonSet(UrlValuesList);
		// ����vm�ļ���Ϣ
		for (File temp : vmFileList) {
			bopVmList.add(new BopVm(temp, proJectPath, UrlValuesList));
			System.out.println("��ȡ�ļ���" + temp.getName() + "ʣ�ࣺ" + --left);
		}

		// ��ʼ��ȡjava�ļ���Ϣ�������ࣩ
		// ��ʼ�����ļ�Map
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
				bopInterfaceMap.put(tempClass.getName(), new BopInterface(javaFile, tempClass, allClass));
			} else {
				bopActionMap.put(tempClass.getName(),
						new BopClass(javaFile, tempClass, allClass, functionName, function));
			}
			System.out.println("ʣ��" + --leftfinal);
		}
		// bopActionMap.putAll(bopClassMap);
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();

		String ignoreInterface = "com.hundsun.jres.workflow.remoting.service.DefinitionService,�ӿ�.com.hundsun.jres.workflow.remoting.service.FormService.δ�ҵ�ʵ����ӿ�.com.hundsun.jres.workflow.remoting.service.InstanceService.δ�ҵ�ʵ����ӿ�.com.hundsun.jres.workflow.remoting.service.TaskService.δ�ҵ�ʵ����com.hundsun.jres.workflow.remoting.service.TaskService,com.hundsun.jres.workflow.remoting.service.InstanceService,com.hundsun.jres.workflow.remoting.service.FormService,com.hundsun.jres.workflow.remoting.service.InstanceService,com.hundsun.jres.workflow.remoting.service.TaskService,com.hundsun.jres.workflow.remoting.service.FormService,com.hundsun.user.biz.impl.cache.CacheManagerImpl,com.hundsun.jresplus.base.cache.CacheManagerImpl,com.hundsun.user.util.SysConfigUtil";
		String ignoreLoad = ignoreInterface
				+ "com.hundsun.jresplus.beans.ObjectFactoryImpl,��.com.hundsun.user.service.sysarg.LicenseManageServiceImpl.δ����.com.hundsun.user.biz.impl.login.LoginManagerImpl.δ����com.hundsun.jresplus.web.url.URLBroker,com.hundsun.jresplus.middleware.MiddlewareServiceImpl,com.hundsun.jresplus.web.servlet.MediaTypesHandler,com.hundsun.jresplus.middleware.MiddlewareServiceImpl,com.hundsun.jresplus.base.dict.DictManagerImpl";

		for (int i = 0; i < stackNum; i++) {
			// ������ע����Ĺ��ܺż���setAll
			for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
				BopClass tempClass = temp.getValue();
				for (BopMethod tempMethod1 : tempClass.getBopSelfMethods()) {
					for (ServiceImpleMethod tempInvokedClaa : tempMethod1.getInvokeMethods()) {
						Class<?> implClass = tempInvokedClaa.getServiceImpl();
						if (implClass != null) {
							BopClass theClass = bopActionMap.get(implClass.getName());
							if (theClass != null) {
								BopMethod theMethod = theClass.getMethodByMethodNameAndParam(
										tempInvokedClaa.getMethodName(), tempInvokedClaa.getParamCount(),
										tempInvokedClaa.getServiceName());
								if (theMethod != null) {
									tempMethod1.functionSetAllAdd(theMethod.getFunctionSetAll());
								} else {
									System.out.println("��." + implClass.getName() + ".δ�ҵ�����."
											+ tempInvokedClaa.getMethodName() + tempInvokedClaa.getParamCount()
											+ "����λ��." + tempMethod1.getMethodClass().getClassName() + "."
											+ tempMethod1.getMethodName());
								}
							} else {
								if (ignoreLoad.indexOf(implClass.getName()) < 0) {
									System.out.println("�����쳣��");
									System.out.println("��." + implClass.getName() + ".δ����");
								}
							}
						} else {
							if (implClass == null) {
								System.out.println("�����쳣��");
								System.out.println("�ӿ�." + tempInvokedClaa.getServiceName() + ".δ�ҵ�ʵ����");
								continue;
							}
						}
					}
				}
			}
		}
		System.out.println("��������");
		System.out.println("----------------------��������----------------------");
		System.out.println("VM����������£�");
		for (BopVm temp : bopVmList) {
			temp.printfSelf();
		}
		System.out.println("�����������£�");
		for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
			temp.getValue().printfSelf();
		}
		System.out.println("�ӿڷ���������£�");
		for (Entry<String, BopInterface> temp : bopInterfaceMap.entrySet()) {
			temp.getValue().printfSelf(bopActionMap);
		}
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("�������������£�");
		Set<String> errorUrl = new HashSet<>();
		// ��������,��ʼ�Ҷ�Ӧ��ϵ
		for (BopVm vm : bopVmList) {
			System.out.println("����" + vm.getVmsName() + "�������:");
			if (vm.getUrlJson().size() == 0) {
				System.out.println("���������");
			}
			String selfJson = vm.getUrlSelfHtm();
			BopMethod methodSelf = findTheMethodByJson(selfJson, bopActionMap);
			if (methodSelf == null) {
			} else {
				System.out.println("�����ʼ��." + selfJson + ".���õĹ��ܺţ�");
				for (String methodRef : methodSelf.getFunctionSetAll()) {
					System.out.println(methodRef);
				}
			}
			for (BopJson json : vm.getUrlJson()) {
				BopMethod method = findTheMethodByJson(json, bopActionMap);
				if (method == null) {
					if (jsonSet.contains(json.getUrlJson())) {
						errorUrl.add(json.getUrlJson());
					}
				} else {
					System.out.println("����." + json.getUrlJson() + ".���õĹ��ܺţ�");
					for (String methodRef : method.getFunctionSetAll()) {
						System.out.println(methodRef);
					}
				}
			}
			System.out.println();
			System.out.println();
			System.out.println();
		}
		System.out.println("----------------------���������----------------------");
		System.out.println();
		System.out.println("��������δ�ҵ���ӦAction���������η���δ������");
		for (String temp : errorUrl) {
			System.out.println(temp);
		}
		System.out.println();
		long t2 = System.currentTimeMillis();
		System.out.println("������ʱ��" + (t2 - t1) + "ms");
		System.out.println("����vm���棺" + bopVmList.size() + "��");
		System.out.println("����Java�ࣺ" + bopActionMap.size() + "��");
		System.out.println("����Java�ӿڣ�" + bopInterfaceMap.size() + "��");
		System.out.println("����Url����" + jsonSet.size() + "��");
		System.out.println("����Function��" + function.size() + "��");

	}

	private static BopMethod findTheMethodByJson(String selfJson, Map<String, BopClass> bopActionMap) {
		for (Entry<String, BopClass> tempEntry : bopActionMap.entrySet()) {
			for (BopMethod tempMethod : tempEntry.getValue().getBopSelfMethods()) {
				if (StringUtils.endsWith(tempMethod.getUrl(), selfJson.trim())) {
					return tempMethod;
				}
			}
		}
		return null;
	}

	private static Set<String> getJsonSet(List<File> urlValuesList) {
		Set<String> res = new HashSet<>();
		for (File temp : urlValuesList) {
			res.addAll(geturl(temp));
		}
		return res;
	}

	private static Collection<? extends String> geturl(File readFile) {
		Set<String> res = new HashSet<>();
		try { // ��ֹ�ļ��������ȡʧ�ܣ���catch��׽���󲢴�ӡ��Ҳ����throw
			File filename = readFile; // Ҫ��ȡ����·����input��txt�ļ�
			InputStreamReader reader = new InputStreamReader(new FileInputStream(filename), "UTF-8"); // ����һ������������reader
			BufferedReader br = new BufferedReader(reader); // ����һ�����������ļ�����ת�ɼ�����ܶ���������
			String line = "";

			line = br.readLine();
			while (line != null) {
				line = line.trim();
				if (!StringUtils.contains(line, "public static") || StringUtils.startsWith(line, "/*")
						|| StringUtils.startsWith(line, "*")) {
					line = br.readLine(); // һ�ζ���һ������
					continue;
				}
				if (StringUtils.isNotBlank(line) && !StringUtils.startsWith(line, "//")) {
					res.add(StringUtils.substringBetween(line, "\"", "\"").trim());
				}
				line = br.readLine(); // һ�ζ���һ������
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return res;
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
