package com.tantian.jsTools;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.tantian.TestModleTools.MapToXmlBean;

public class TransFormJsToTestXML {

	private static String FilePath = "C:\\Users\\Administrator\\Documents\\功能号测试用例";

	public static void main(String[] args) throws IOException {
		changeJsToModel changejsToModel = new changeJsToModel();
		MapToXmlBean mapToXmlBean = new MapToXmlBean();
		try {
			while (true) {
				System.out.println("请输入功能号：");
				BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
				String function_id = br.readLine();
				System.out.println("请输入用例对应的Map或JsList");
				String Test_Map = br.readLine();
				if(Test_Map.contains("=")){
					mapToXmlBean.createXmlModel(function_id, Test_Map, FilePath);
				}else{
					String temp = changejsToModel.TransFormJsToMap(br);
					mapToXmlBean.createXmlModel(function_id, temp, FilePath);
				}
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

}
