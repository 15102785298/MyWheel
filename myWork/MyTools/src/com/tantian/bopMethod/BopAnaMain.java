package com.tantian.bopMethod;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.tantian.bopModel.BopVm;
import com.tantian.bopUtils.FileUtils;

public class BopAnaMain {

	public static void main(String[] args) {
		/*
		 * ��ʼ��ȡvm�ļ���Ϣ
		 */
		String proJectPath = "D://zt//BOP//Sources//WebCodes//bop2.0";
		List<File> vmFileList = FileUtils.getAllFile(proJectPath, ".vm");
		List<BopVm> bopVmList = new LinkedList<>();
		int left = vmFileList.size();
		List<File> UrlValuesList = FileUtils.getAllFile(proJectPath, "UrlValues.java");
		for (File temp : vmFileList) {
			bopVmList.add(new BopVm(temp, proJectPath, UrlValuesList));
			System.out.println("��ȡ�ļ���" + temp.getName() + "ʣ�ࣺ" + --left);
		}
		// ����vm�ļ���Ϣ
		for (BopVm temp : bopVmList) {
			temp.printfSelf();
		}
		// ��ʼ��ȡjava�ļ���Ϣ�������ࣩ

	}

}