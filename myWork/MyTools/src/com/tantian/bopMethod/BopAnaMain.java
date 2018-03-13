package com.tantian.bopMethod;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.text.html.parser.Entity;

import org.apache.commons.lang3.StringUtils;

import com.tantian.bopModel.BopClass;
import com.tantian.bopModel.BopInterface;
import com.tantian.bopModel.BopVm;
import com.tantian.bopUtils.BopClassLoaderUtils;
import com.tantian.bopUtils.FileUtils;

public class BopAnaMain {

	public static void main(String[] args) {
		/*
		 * 开始读取vm文件信息
		 */
		String proJectPath = "D://zt//BOP//Sources//WebCodes//bop2.0";
		List<File> vmFileList = FileUtils.getAllFile(proJectPath, ".vm");
		List<File> javaFileList = FileUtils.getAllFile(proJectPath, ".java");
		Map<String, File> javaFileMap = new HashMap<>();

		Map<String, BopVm> bopVmMap = new HashMap<>();
		Map<String, BopInterface> bopInterfaceMap = new HashMap<>();
		Map<String, BopClass> bopClassMap = new HashMap<>();
		Map<String, BopClass> bopActionMap = new HashMap<>();
		/*
		 * int left = vmFileList.size(); List<File> UrlValuesList =
		 * FileUtils.getAllFile(proJectPath, "UrlValues.java");
		 *
		 * for (File temp : vmFileList) { bopVmList.add(new BopVm(temp,
		 * proJectPath, UrlValuesList)); System.out.println("读取文件：" +
		 * temp.getName() + "剩余：" + --left); } // 输入vm文件信息 for (BopVm temp :
		 * bopVmList) { temp.printfSelf(); }
		 */

		// 开始读取java文件信息（加载类）
		// 初始化类文件Map
		for (File temp : javaFileList) {
			javaFileMap.put(StringUtils.split(temp.getName(), ".")[0], temp);
		}
		BopClassLoaderUtils classLoader = new BopClassLoaderUtils(FileUtils.getAllFile("D:\\aaWork\\WEB-INF", ".jar"));
		Map<String, Class<?>> allClass = classLoader.getAllClass();
		for (Entry<String, Class<?>> temp : allClass.entrySet()) {
			Class<?> tempClass = temp.getValue();
			String[] classNameList = StringUtils.split(tempClass.getName(), ".");
			File javaFile = javaFileMap.get(classNameList[classNameList.length - 1]);
			if (javaFile == null) {
				continue;
			}
			if (tempClass.isInterface()) {
				bopInterfaceMap.put(classNameList[classNameList.length - 1], new BopInterface(javaFile, tempClass));
			} else if (isAction(tempClass)) {
				bopActionMap.put(classNameList[classNameList.length - 1], new BopClass(javaFile, tempClass));
			} else {
				bopClassMap.put(classNameList[classNameList.length - 1], new BopClass(javaFile, tempClass));
			}
		}
		for (Entry<String, BopInterface> temp : bopInterfaceMap.entrySet()) {
			temp.getValue().printfSelf();
		}
		System.out.println("输出类-----------------");
		for (Entry<String, BopClass> temp : bopActionMap.entrySet()) {
			temp.getValue().printfSelf();
		}
		for (Entry<String, BopClass> temp : bopClassMap.entrySet()) {
			temp.getValue().printfSelf();
		}
	}

	private static boolean isAction(Class<?> tempClass) {
		for (Annotation temp : tempClass.getAnnotations()) {
			if (StringUtils.endsWith(temp.getClass().getName(), "Controller")) {
				return true;
			}
		}
		return false;
	}

}
