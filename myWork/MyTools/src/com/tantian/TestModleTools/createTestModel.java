package com.tantian.TestModleTools;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.tantian.TestModleTools.MapToXmlBean;

public class createTestModel {

	private static String FilePath = "C:\\Users\\Administrator\\Documents\\���ܺŲ�������";

	public static void main(String[] args) throws Exception {
		MapToXmlBean mapToXmlBean = new MapToXmlBean();
		try {
			while (true) {
				System.out.println("�����빦�ܺţ�");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String function_id = br.readLine();
				System.out.println("������������Ӧ��Map: ");
				String Test_Map = br.readLine();
				mapToXmlBean.createXmlModel(function_id, Test_Map, FilePath);
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			throw e;
		}
	}

}
